package edu.rmit.highlandmimic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SimpleInternalCredentialService {

    private final Map<String, String> resetRequestCredentials;

    public String issueAndPersistResetCredentials(String userEmail) {
        String uuid = UUID.randomUUID().toString();

        this.resetRequestCredentials.put(uuid, userEmail);

        return uuid;
    }

    public String acceptResetCredential(String credential) {

        if (!resetRequestCredentials.containsKey(credential)) {
            throw new NoSuchElementException("Invalid or unmatched credential");
        }

        return resetRequestCredentials.remove(credential);
    }

}

