package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.User;
import edu.rmit.highlandmimic.model.request.AuthenticationRequestEntity;
import edu.rmit.highlandmimic.model.request.UserRequestEntity;
import edu.rmit.highlandmimic.model.response.AuthenticationResponseEntity;
import edu.rmit.highlandmimic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityHandler securityHandler;

    // READ operations

    @GetMapping("/issue-rspwmail")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String forward) {
        return controllerWrapper(() -> userService.issueResetPasswordMail(email, forward));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                userService::getAllUsers,
                SecurityHandler.ALLOW_AUTHORITIES
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByIdentity(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
                                                   @RequestParam String q) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                () -> userService.searchUserByIdentity(q),
                SecurityHandler.ALLOW_AUTHORITIES
        );
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<User>> searchUsersByDisplayName(@RequestParam String q) {
        return ResponseEntity.ok(userService.searchUsersByDisplayName(q));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestEntity reqEntity) {
        return controllerWrapper(() -> userService.login(reqEntity));
    }

    @PostMapping("/login-oauth2")
    public ResponseEntity<AuthenticationResponseEntity> loginViaOAuth2Provider(@RequestBody AuthenticationRequestEntity reqEntity) {
//        return ResponseEntity.ok(userService.loginViaOAuth2Provider(reqEntity));
        return null;
    }

    // WRITE operation

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody UserRequestEntity reqEntity) {
        return controllerWrapper(() -> userService.createNewUser(reqEntity));
    }

    @PostMapping("/signup-oauth2")
    public ResponseEntity<?> createNewUserWithOAuth2Provider(@RequestBody UserRequestEntity reqEntity) {
        return controllerWrapper(() -> userService.createNewUser(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{identity}/{fieldName}")
    public ResponseEntity<?> updateFieldValueOfExistingUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
                                                            @PathVariable String identity,
                                                            @PathVariable String fieldName,
                                                            @RequestBody Object newValue) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                () -> userService.updateFieldValueOfExistingUser(identity, fieldName, newValue),
                SecurityHandler.ALLOW_AUTHORITIES
        );
    }

    @PostMapping("/{identity}/role")
    public ResponseEntity<?> changeRoleOfExistingUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
                                                      @PathVariable String identity,
                                                      @RequestBody String newRole) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                () -> userService.changeRoleOfExistingUser(identity, newRole),
                List.of(User.UserRole.ADMIN)
        );
    }

    @PostMapping("/reset-pass")
    public ResponseEntity<?> resetNewPasswordForExistingUser(@RequestParam String resetCredentials,
                                                             @RequestBody String newHashedPassword) {
        return controllerWrapper(() -> userService.resetNewPasswordForExistingUser(resetCredentials, newHashedPassword));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<?> removeAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                userService::removeAllUsers,
                List.of(User.UserRole.ADMIN)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
                                            @PathVariable String id) {
        return securityHandler.roleGuarantee(
                authorizationToken,
                () -> userService.removeUserById(id),
                SecurityHandler.ALLOW_AUTHORITIES
        );
    }

}