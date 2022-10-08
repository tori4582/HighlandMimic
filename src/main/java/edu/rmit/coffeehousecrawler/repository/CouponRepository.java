package edu.rmit.coffeehousecrawler.repository;

import edu.rmit.coffeehousecrawler.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public class CouponRepository {

    private final String requestUrl;

    public CouponRepository(@Value("${infrastructure.services.coffeehouse.coupons.retrieve.url}") String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Flux<Coupon> getCoupons() {
        return WebClient.create()
                .get()
                .uri(requestUrl)
                .retrieve()
                .bodyToFlux(Coupon.class);
    }

}
