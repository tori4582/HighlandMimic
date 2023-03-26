package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@Document("users")
public class User {

    @Id
    private String userId;

    @NonNull
    private String username;

    private String displayName;

    @Builder.Default
    private Boolean isMale = true;

    @NonNull
    private String email;
    private String phoneNumber;

    @NonNull
    private String hashedPassword;

    private String imageUrl;

    @Builder.Default

    private UserRole userRole = UserRole.CUSTOMER;

    @Builder.Default
    private AccountProvider accountProvider = AccountProvider.SELF_PROVIDED;

    private String accountProviderReferenceUid;

    public enum UserRole {
        NO_ROLE,
        CUSTOMER,
        STAFF,
        ADMIN
    }

    public enum AccountProvider {
        FACEBOOK,
        GOOGLE,
        SELF_PROVIDED
    }

}
