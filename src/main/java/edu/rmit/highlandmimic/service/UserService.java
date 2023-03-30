package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.common.JwtUtils;
import edu.rmit.highlandmimic.model.User;
import edu.rmit.highlandmimic.model.request.AuthenticationRequestEntity;
import edu.rmit.highlandmimic.model.request.OAuth2AuthenticationRequestEntity;
import edu.rmit.highlandmimic.model.request.UserRequestEntity;
import edu.rmit.highlandmimic.repository.UserRepository;
import lombok.SneakyThrows;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import static edu.rmit.highlandmimic.common.CommonUtils.getOrDefault;
import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final SimpleInternalCredentialService simpleInternalCredentialService;

    private final String resetPasswordEndpoint;

    public UserService(@Value("${server.endpoint}") String resetPasswordEndpoint,
                       @Autowired UserRepository userRepository,
                       @Autowired MailService mailService,
                       @Autowired SimpleInternalCredentialService simpleInternalCredentialService
                       ) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.simpleInternalCredentialService = simpleInternalCredentialService;
        this.resetPasswordEndpoint = resetPasswordEndpoint;
    }


    /* Test only */
    public long removeAllUsers() {
        long quantity = userRepository.count();
        userRepository.deleteAll();
        return quantity;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User searchUserByIdentity(String q) {
        return Optional.ofNullable(userRepository.findByEmailIgnoreCase(q))
                .or(() -> Optional.ofNullable(userRepository.findByUsernameIgnoreCase(q)))
                .or(() -> Optional.ofNullable(userRepository.findByPhoneNumber(q)))
                .orElse(null);
    }

    public List<User> searchUsersByDisplayName(String displayName) {
        return userRepository.findAllByDisplayNameContainingIgnoreCase(displayName);
    }

    public Object createNewUser(UserRequestEntity reqEntity) {
        // check if there is already an existing user in database
        // require that the user need to register with email firstly via SELF_PROVIDED method
        if (Objects.nonNull(this.searchUserByIdentity(reqEntity.getEmail()))) {
            throw new IllegalArgumentException(String.format("User with given email '%s' is already exists", reqEntity.getEmail()));
        }
        if (Objects.nonNull(this.searchUserByIdentity(reqEntity.getPhoneNumber()))) {
            throw new IllegalArgumentException(String.format("User with given phone number '%s' is already exists", reqEntity.getPhoneNumber()));
        }
        if (Objects.nonNull(this.searchUserByIdentity(reqEntity.getUsername()))) {
            throw new IllegalArgumentException(String.format("User with given username '%s' is already exists", reqEntity.getUsername()));
        }

        User preparedUser = User.builder()
                .username(reqEntity.getUsername())
                .displayName(reqEntity.getDisplayName())
                .isMale((Boolean) getOrDefault(reqEntity.getIsMale(), true))
                .email(reqEntity.getEmail())
                .phoneNumber(reqEntity.getPhoneNumber())
                .hashedPassword(reqEntity.getHashedPassword())
                .userRole((User.UserRole) getOrDefault(reqEntity.getUserRole(), User.UserRole.CUSTOMER))
                .associatedProviders(new HashMap<>())
                .imageUrl(reqEntity.getImageUrl())
                .build();

        User persistedUser = userRepository.save(preparedUser);
        persistedUser.getAssociatedProviders().put(User.AccountProvider.SELF_PROVIDED, persistedUser.getUserId());

        return userRepository.save(persistedUser);
    }

    public User linkAccountWithAssociatedProvider(OAuth2AuthenticationRequestEntity reqEntity) {

        User loadedUser = this.searchUserByIdentity(reqEntity.getOauth2ProviderUserIdentity());
        if (Objects.isNull(loadedUser)) {
            throw new NullPointerException("Invalid userId");
        }

        User.AccountProvider oauth2Provider = User.AccountProvider.valueOf(reqEntity.getOauth2ProviderProviderName());
        String associatedUserId = loadedUser.getAssociatedProviders().getOrDefault(oauth2Provider, null);

        if (Objects.nonNull(associatedUserId)) {
            throw new NullPointerException("Already linked account, invalid request. Please consider to unlink associated provider before renewing it.");
        }

        loadedUser.getAssociatedProviders().put(oauth2Provider, reqEntity.getOauth2ProviderUserId());

        return userRepository.save(loadedUser);
    }

    public User unlinkAccountWithAssociatedProvider(String userId, String associatedProviderName) {
        User loadedUser = this.getUserById(userId);
        if (Objects.isNull(loadedUser)) {
            throw new NullPointerException("Invalid user identity. This could be caused since the user is not linked with any OAuth2 provider.");
        }

        User.AccountProvider oauth2Provider = User.AccountProvider.valueOf(associatedProviderName);
        String associatedUserId = loadedUser.getAssociatedProviders().getOrDefault(oauth2Provider, null);

        if (Objects.isNull(associatedUserId)) {
            throw new NullPointerException("Invalid OAuth2 provider name. This could be caused since the user is not linked with any OAuth2 provider.");
        }

        loadedUser.getAssociatedProviders().remove(oauth2Provider);
        return userRepository.save(loadedUser);
    }

    public Map<String, Object> loginViaOAuth2Provider(String userId, OAuth2AuthenticationRequestEntity reqEntity) {

        User loadedUser = this.getUserById(userId);
        if (Objects.isNull(loadedUser)) {
            throw new NullPointerException("Invalid user identity. This could be caused since the user is not linked with any OAuth2 provider.");
        }

        User.AccountProvider oauth2Provider = User.AccountProvider.valueOf(reqEntity.getOauth2ProviderProviderName());
        String associatedUserId = loadedUser.getAssociatedProviders().getOrDefault(oauth2Provider, null);

        if (Objects.isNull(associatedUserId)) {
            throw new NullPointerException("Invalid OAuth2 provider name. This could be caused since the user is not linked with any OAuth2 provider.");
        }

        if (!associatedUserId.equals(reqEntity.getOauth2ProviderUserId())) {
            throw new NullPointerException("Invalid persisted associated userId. This could be caused since the user is not linked with any OAuth2 provider.");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", JwtUtils.issueAuthenticatedAccessToken(loadedUser));
        result.put("userDocumentEntity", loadedUser);

        return result;
    }

    public Map<String, Object> login(AuthenticationRequestEntity reqEntity) {
        User loadedUser = this.searchUserByIdentity(reqEntity.getLoginIdentity());

        if (Objects.isNull(loadedUser) || !loadedUser.getHashedPassword().equals(reqEntity.getHashedPassword())) {
            throw new NullPointerException("Invalid username or password");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", JwtUtils.issueAuthenticatedAccessToken(loadedUser));
        result.put("userDocumentEntity", loadedUser);

        return result;
    }

    public Object removeUserById(String id) {
        return userRepository.findById(id)
                .map(loadedEntity -> {
                    userRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public User updateExistingUser(String identity, UserRequestEntity reqEntity) {
        User preparedProduct = ofNullable(this.searchUserByIdentity(identity))
                .map(loadedEntity -> {
                    loadedEntity.setUsername(reqEntity.getUsername());
                    loadedEntity.setDisplayName(reqEntity.getDisplayName());
                    loadedEntity.setIsMale((Boolean) getOrDefault(reqEntity.getIsMale(), true));
                    loadedEntity.setEmail(reqEntity.getEmail());
                    loadedEntity.setPhoneNumber(reqEntity.getPhoneNumber());
                    loadedEntity.setHashedPassword(reqEntity.getHashedPassword());
                    loadedEntity.setImageUrl(reqEntity.getImageUrl());
                    loadedEntity.setUserRole((User.UserRole) getOrDefault(reqEntity.getUserRole(), User.UserRole.CUSTOMER));
//                    loadedEntity.setAccountProvider(
//                            (User.AccountProvider) getOrDefault(reqEntity.getAccountProvider(), User.AccountProvider.SELF_PROVIDED)
//                    );

                    return loadedEntity;
                }).orElseThrow();

        return userRepository.save(preparedProduct);
    }

    @SneakyThrows
    public User updateFieldValueOfExistingUser(String identity, String fieldName, Object newValue) {

        final List<String> unmodifiableFields = List.of("userId", "userRole", "accountProvider");
        if (!unmodifiableFields.contains(fieldName)) {
            throw new IllegalAccessException("Invalid action, unable to update unmodifiable field");
        }

        User preparedUser = Optional.ofNullable(this.searchUserByIdentity(identity)).orElseThrow();

        Field preparedField = preparedUser.getClass().getDeclaredField(fieldName);
        preparedField.setAccessible(true);

        log.info("Performs updating field value: "
                + identity + "::" + "User$"
                + preparedField.getName() + "::" + preparedField.getType().getSimpleName()
                + " : " + preparedField.get(preparedUser)
                + " => " + newValue
        );

        preparedField.set(preparedUser, newValue);

        return userRepository.save(preparedUser);
    }

    @SneakyThrows
    public void issueResetPasswordMail(String emailAddress, String forwardResetClientUrl) {
        User receiver = Optional.ofNullable(userRepository.findByEmailIgnoreCase(emailAddress))
                .orElseThrow(() -> {throw new NullPointerException("Invalid email address: " + emailAddress);});

        String credential = this.simpleInternalCredentialService.issueAndPersistResetCredentials(receiver.getUsername());

        if (forwardResetClientUrl.contains("?")) {
            forwardResetClientUrl += forwardResetClientUrl.contains("?") ? "&" : "?";
        }

        String resetPasswordPath =  forwardResetClientUrl
                + (forwardResetClientUrl.contains("?") ? "&" : "?")
                + "resetCredential=" + credential;
        mailService.issueResetPassword(receiver, resetPasswordPath);
    }

    public Object resetNewPasswordForExistingUser(String resetCredential, String newHashedPassword) {
        String userEmail = simpleInternalCredentialService.acceptResetCredential(resetCredential);
        User preparedUser = userRepository.findByEmailIgnoreCase(userEmail);

        preparedUser.setHashedPassword(newHashedPassword);

        return userRepository.save(preparedUser);
    }

    public User changeRoleOfExistingUser(String identity, String newRole) {
        User preparedUser = Optional.ofNullable(this.searchUserByIdentity(identity)).orElseThrow();

        preparedUser.setUserRole(User.UserRole.valueOf(newRole));

        return userRepository.save(preparedUser);
    }
}
