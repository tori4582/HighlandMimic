package edu.rmit.coffeehousecrawler.service;

import edu.rmit.coffeehousecrawler.model.Coupon;
import edu.rmit.coffeehousecrawler.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> retrieveAllCoupons() {
        return couponRepository.getCoupons()
                .collectList()
                .block();
    }

    public Coupon retrieveCoupon(String id) {
        return Optional.ofNullable(couponRepository.getCoupons()
                .filter(coupon -> coupon.getId().equalsIgnoreCase(id))
                .blockFirst()
        ).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
    }

}
