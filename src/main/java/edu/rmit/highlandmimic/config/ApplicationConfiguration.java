package edu.rmit.highlandmimic.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class ApplicationConfiguration {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }

    @EventListener
    void onStartup(ApplicationReadyEvent event) {
        log.info("[SERVER_STATE_NOTIFICATION] " + "\u001B[32m" + "✅ Server is up and ready to accept requests 👌" + "\u001B[0m");
    }

    @EventListener
    void onShutdown(ContextStoppedEvent event) {
        // do sth
    }

}

