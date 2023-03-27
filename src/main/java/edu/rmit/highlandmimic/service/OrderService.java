package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Order;
import edu.rmit.highlandmimic.model.Product;
import edu.rmit.highlandmimic.model.request.OrderRequestEntity;
import edu.rmit.highlandmimic.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static edu.rmit.highlandmimic.common.CommonUtils.getOrDefault;
import static edu.rmit.highlandmimic.model.mapping.ModelMappingHandlers.convertListOfIdsToCoupons;
import static edu.rmit.highlandmimic.model.mapping.ModelMappingHandlers.convertListOfIdsToProducts;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CouponService couponService;
    private final StoreService storeService;


    public List<Order> getOrdersOfUser(String userIdentity, String status) {
        return Optional.ofNullable(userService.searchUserByIdentity(userIdentity))
                .map(loadedUser -> Strings.isBlank(status)
                        ? orderRepository.getOrdersByUserIdContains(loadedUser.getUserId())
                        : orderRepository.getOrdersByUserIdContains(loadedUser.getUserId())
                                .stream()
                                .filter(e -> e.getOrderStatus().equals(Order.OrderStatus.valueOf(status.toUpperCase())))
                                .toList()
                ).orElseThrow();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createNewOrder(OrderRequestEntity reqEntity) {
        Order preparedOrder = Order.builder()
                .userId(reqEntity.getUserId())
                .orderItems(reqEntity.getOrderItems())
                .selectedPaymentMethod((Order.PaymentMethod) getOrDefault(reqEntity.getPaymentMethod(), Order.PaymentMethod.CASH))
                .selectedPickupOption((Order.PickupOption) getOrDefault(reqEntity.getPickupOptions(), Order.PickupOption.AT_STORE))
                .appliedCoupons(convertListOfIdsToCoupons(couponService.getAllCoupons(), reqEntity.getCouponIds()))
                .selectedPickupStore(storeService.getStoreById(reqEntity.getStoreId()))
                .orderStatus(Order.OrderStatus.PENDING)
                .address1(reqEntity.getAddress1())
                .address2(reqEntity.getAddress2())
                .address3(reqEntity.getAddress3())
                .address4(reqEntity.getAddress4())
                .build();

        return orderRepository.save(preparedOrder);
    }

    private static Map<Product, Integer> convertMapOfProductIdsToMapOfProducts(List<Product> referenceProducts, Map<String, Integer> productIdsMap) {
        List<Product> products = convertListOfIdsToProducts(referenceProducts, new ArrayList<>(productIdsMap.keySet()));
        Map<Product, Integer> preparedResultMap = new HashMap<>();

        products.forEach(product -> preparedResultMap.put(product, productIdsMap.get(product.getProductId())));
        return preparedResultMap;
    }

    public Object attachCouponsOntoOrder(String id, List<String> couponIds) {
        return Optional.ofNullable(this.getOrderById(id))
                .map(loadedEntity -> {
                    loadedEntity.setAppliedCoupons(convertListOfIdsToCoupons(couponService.getAllCoupons(), couponIds));
                    return orderRepository.save(loadedEntity);
                })
                .orElseThrow();
    }

    @SneakyThrows
    private Order changeStateOfOrder(String id,
                                    Order.OrderStatus destinationStatus,
                                    Predicate<Order> assignmentPrecondition,
                                    Supplier<? extends Exception> exceptionWhenCheckFailed) {
        Order loadedOrder = Optional.ofNullable(this.getOrderById(id)).orElseThrow();
        return Optional.of(loadedOrder).filter(assignmentPrecondition)
                .map(loadedEntity -> {
                    loadedEntity.setOrderStatus(destinationStatus);
                    return orderRepository.save(loadedEntity);
                })
                .orElseThrow(exceptionWhenCheckFailed);
    }

    public Order closeSuccessfulOrder(String id) {
        return this.changeStateOfOrder(
                id,
                Order.OrderStatus.COMPLETED,
                order -> order.getOrderStatus().equals(Order.OrderStatus.PLACED),
                () -> new IllegalStateException("Cannot close an order which is not in 'PLACED' state")
        );
    }

    public Order cancelOrder(String id) {
        return this.changeStateOfOrder(
                id,
                Order.OrderStatus.CANCELLED,
                order -> order.getOrderStatus().equals(Order.OrderStatus.PLACED),
                () -> new IllegalStateException("Cannot cancel an order which is not in 'PLACED' state")
        );
    }

    public Object placeOrder(String id) {
        return this.changeStateOfOrder(
            id,
            Order.OrderStatus.PLACED,
            order -> !order.getOrderStatus().equals(Order.OrderStatus.PLACED),
            () -> new IllegalStateException("Cannot place an order which is already in 'PLACED' state")
        );
    }

    public Order updatePendingOrder(String id, OrderRequestEntity reqEntity) {
        Order loadedOrder = Optional.ofNullable(this.getOrderById(id)).orElseThrow();

        Order preparedOrder = Optional.of(loadedOrder)
                .filter(order -> order.getOrderStatus().equals(Order.OrderStatus.PENDING))
                .map(loadedEntity -> {
                    loadedEntity.setOrderItems(reqEntity.getOrderItems());
                    loadedEntity.setAppliedCoupons(convertListOfIdsToCoupons(couponService.getAllCoupons(), reqEntity.getCouponIds()));
                    loadedEntity.setSelectedPickupStore(storeService.getStoreById(reqEntity.getStoreId()));
                    loadedEntity.setSelectedPaymentMethod(reqEntity.getPaymentMethod());
                    loadedEntity.setSelectedPickupOption(reqEntity.getPickupOptions());
                    loadedEntity.setAddress1(reqEntity.getAddress1());
                    loadedEntity.setAddress2(reqEntity.getAddress2());
                    loadedEntity.setAddress3(reqEntity.getAddress3());
                    loadedEntity.setAddress4(reqEntity.getAddress4());

                    return loadedEntity;
                }).orElseThrow();

        return orderRepository.save(preparedOrder);
    }
}
