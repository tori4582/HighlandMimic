package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Coupon;
import edu.rmit.highlandmimic.model.Order;
import edu.rmit.highlandmimic.model.ProductCatalogue;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static edu.rmit.highlandmimic.common.CommonUtils.getOrDefault;
import static edu.rmit.highlandmimic.common.ModelMappingHandlers.associationGuardianBeforeTakingAction;
import static java.util.Optional.ofNullable;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final OrderService orderService;

    @Autowired
    public CouponService(CouponRepository couponRepository, @Lazy OrderService orderService) {
        this.couponRepository = couponRepository;
        this.orderService = orderService;
    }

    @SneakyThrows
    public static Long getCouponDiscountAmount(Long totalOrderAmount, Coupon appliedCoupon) {

        if (Objects.isNull(appliedCoupon)) {
            return 0L;
        }

        if (Objects.nonNull(appliedCoupon.getMinimumOrderAmountCriterion())
                && totalOrderAmount < appliedCoupon.getMinimumOrderAmountCriterion()) {
            throw new Exception("Your order is not eligible for this coupon: Not enough order amount");
        }

        Long discountedAmount = 0L;
        if (Objects.nonNull(appliedCoupon.getDiscountRate())) {
            discountedAmount += Math.round(appliedCoupon.getDiscountRate() * totalOrderAmount);
            return (Math.abs(discountedAmount) > appliedCoupon.getDiscountRateCapAmount())
                    ? appliedCoupon.getDiscountRateCapAmount()
                    : discountedAmount;
        }
        return (Long) getOrDefault(appliedCoupon.getDiscountAmount(), 0L);
    }

    // READ operations

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(String id) {
        return Optional.ofNullable(id).flatMap(couponRepository::findById).orElse(null);
    }

    public List<Coupon> searchCouponsByName(String nameQuery) {
        return couponRepository.getCouponsByCouponNameContainsIgnoreCase(nameQuery);
    }

    // WRITE operations

    public Coupon createNewCoupon(CouponRequestEntity reqEntity) {
        Coupon preparedCoupon = Coupon.builder()
                .couponName(reqEntity.getName())
                .couponCode(reqEntity.getCode())
                .content(reqEntity.getContent())
                .imageUrl(reqEntity.getImageUrl())
                .dueDate(reqEntity.getDueDate())
                .discountRate(reqEntity.getRate())
                .discountAmount(reqEntity.getAmount())
                .discountRateCapAmount(reqEntity.getCapAmount())
                .minimumOrderAmountCriterion(reqEntity.getMinimumAmountCriterion())
                .build();

        return couponRepository.save(preparedCoupon);
    }

    // MODIFY operations

    public Coupon updateExistingCoupon(String id, CouponRequestEntity reqEntity) {

        Coupon preparedCoupon = ofNullable(this.getCouponById(id))
                .map(loadedEntity -> {
                    loadedEntity.setCouponName(reqEntity.getName());
                    loadedEntity.setCouponCode(reqEntity.getCode());
                    loadedEntity.setContent(reqEntity.getContent());
                    loadedEntity.setImageUrl(reqEntity.getImageUrl());
                    loadedEntity.setDueDate(reqEntity.getDueDate());
                    loadedEntity.setDiscountRate(reqEntity.getRate());
                    loadedEntity.setDiscountAmount(reqEntity.getAmount());
                    loadedEntity.setDiscountRateCapAmount(reqEntity.getCapAmount());
                    loadedEntity.setMinimumOrderAmountCriterion(reqEntity.getMinimumAmountCriterion());

                    return loadedEntity;
                }).orElseThrow();

        return couponRepository.save(preparedCoupon);
    }

    @SneakyThrows
    public Coupon updateFieldValueOfExistingCoupon(String id, String fieldName, Object newValue) {

        Coupon preparedCoupon =  ofNullable(this.getCouponById(id)).orElseThrow();

        Field preparedField = preparedCoupon.getClass().getDeclaredField(fieldName);
        preparedField.setAccessible(true);
        preparedField.set(preparedCoupon, newValue);

        return couponRepository.save(preparedCoupon);
    }

    // DELETE operations

    public Coupon removeCouponById(String id) {

        Objects.requireNonNull(couponRepository.findById(id));

        associationGuardianBeforeTakingAction(
                id, orderService.getOrdersOfUser("", ""),
                (order) -> Optional.ofNullable(((Order) order).getAppliedCoupon()),
                Coupon::getCouponCode
        );

        return couponRepository.findById(id)
                .map(loadedEntity -> {
                    couponRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllCoupons() {
        long quantity = couponRepository.count();
        couponRepository.deleteAll();
        return quantity;
    }

}