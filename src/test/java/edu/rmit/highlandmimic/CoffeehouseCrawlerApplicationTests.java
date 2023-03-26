package edu.rmit.highlandmimic;

import edu.rmit.highlandmimic.common.JwtUtils;
import edu.rmit.highlandmimic.model.User;
import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@ContextConfiguration
@SpringBootTest
class CoffeehouseCrawlerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getContentFromUrl() {
        String encodedJwt = JwtUtils.issueAuthenticatedAccessToken(User.builder()
                .userId("testid")
                .username("test")
                .hashedPassword("hehe")
                .email("mockEmail")
                .userRole(User.UserRole.ADMIN)
                .build());

//        String decodedJwt = JwtUtils.decodeJwtToken(encodedJwt);
//        System.out.println(decodedJwt.toString());
    }

}
