package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Coupon;
import edu.rmit.highlandmimic.model.ProductCatalogue;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    // READ operations

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(String id) {
        return couponRepository.findById(id).orElse(null);
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