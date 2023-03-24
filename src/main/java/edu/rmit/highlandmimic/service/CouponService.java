package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Coupon;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return couponRepository.getCouponsByCouponNameContains(nameQuery);
    }

    // WRITE operations

    public Coupon createNewCoupon(CouponRequestEntity reqEntity) {
        Coupon preparedCoupon = Coupon.builder()
                .build();

        return couponRepository.save(preparedCoupon);
    }

    // MODIFY operations

    public Coupon updateExistingCoupon(String id, CouponRequestEntity reqEntity) {

        Coupon preparedCoupon = ofNullable(this.getCouponById(id))
                .map(loadedEntity -> {
                    Coupon CouponObj = Coupon.builder()
                            .build();


                    return CouponObj;
                }).orElseThrow();

//        return couponRepository.update(preparedCoupon);
        return null;
    }

    public Coupon updateFieldValueOfExistingCoupon(String id, String fieldName, Object newValue) {
        Coupon preparedCoupon =  ofNullable(this.getCouponById(id))
                .map(loadedEntity -> {
                    return Coupon.builder().build();
                }).orElseThrow();

//        return couponRepository.update(preparedCoupon);
        return null;
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