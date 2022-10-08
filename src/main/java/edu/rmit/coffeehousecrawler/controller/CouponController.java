package edu.rmit.coffeehousecrawler.controller;

import edu.rmit.coffeehousecrawler.model.Coupon;
import edu.rmit.coffeehousecrawler.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/")
    public ResponseEntity<List<Coupon>> getCoupons() {
        return ResponseEntity.ok(couponService.retrieveAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(couponService.retrieveCoupon(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
