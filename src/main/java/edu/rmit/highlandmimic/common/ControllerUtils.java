package edu.rmit.highlandmimic.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static edu.rmit.highlandmimic.common.ExceptionLogger.logInvalidAction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerUtils {
    public static ResponseEntity<?> controllerWrapper(Supplier<?> controllerExecution) {
        try {
            return ResponseEntity.ok(controllerExecution.get());
        } catch (Exception e) {
            logInvalidAction(e);
            return switchExceptionsResponse(e);
        }
    }

    private static ResponseEntity<?> switchExceptionsResponse(Exception e) {
        if (e instanceof NoSuchElementException) {
            return ResponseEntity.notFound().build();
        }
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.internalServerError().body(ExceptionLogger.ResponseException.fromExceptionObject(e));
    }
}
