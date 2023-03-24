package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.Coupon;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/Coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // READ operations

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable String id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Coupon>> searchCouponsByName(@RequestParam String q) {
        return ResponseEntity.ok(couponService.searchCouponsByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewCoupon(@RequestBody CouponRequestEntity reqEntity) {
        return controllerWrapper(() -> couponService.createNewCoupon(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingCoupon(@PathVariable String id, @RequestBody CouponRequestEntity reqEntity) {
        return controllerWrapper(() -> couponService.updateExistingCoupon(id, reqEntity));
    }

    @PostMapping("/E/{id}/{fieldName}")
    public ResponseEntity<?> updateFieldValueOfExistingCoupon(@PathVariable String id,
                                                          @PathVariable String fieldName,
                                                          @RequestBody Object newValue) {
        return controllerWrapper(() -> couponService.updateFieldValueOfExistingCoupon(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllCoupons() {
        return ResponseEntity.ok(couponService.removeAllCoupons());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCouponById(@PathVariable String id) {
        return controllerWrapper(() -> couponService.removeCouponById(id));
    }

}