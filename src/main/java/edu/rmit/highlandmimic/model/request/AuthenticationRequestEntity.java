package edu.rmit.highlandmimic.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequestEntity {
    private String loginIdentity;
    private String hashedPassword;
}
