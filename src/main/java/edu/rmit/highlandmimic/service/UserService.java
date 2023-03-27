package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.common.JwtUtils;
import edu.rmit.highlandmimic.model.User;
import edu.rmit.highlandmimic.model.request.AuthenticationRequestEntity;
import edu.rmit.highlandmimic.model.request.UserRequestEntity;
import edu.rmit.highlandmimic.repository.UserRepository;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        if (Objects.nonNull(userRepository.findByEmailIgnoreCase(reqEntity.getEmail()))) {
            throw new IllegalArgumentException(
                    "User with email '%s' is already existed".formatted(reqEntity.getEmail()));
        }

        User preparedUser = User.builder()
                .username(reqEntity.getUsername())
                .displayName(reqEntity.getDisplayName())
                .isMale((Boolean) getOrDefault(reqEntity.getIsMale(), true))
                .email(reqEntity.getEmail())
                .phoneNumber(reqEntity.getPhoneNumber())
                .hashedPassword(reqEntity.getHashedPassword())
                .userRole((User.UserRole) getOrDefault(reqEntity.getUserRole(), User.UserRole.CUSTOMER))
                .accountProvider(
                        (User.AccountProvider) getOrDefault(reqEntity.getAccountProvider(), User.AccountProvider.SELF_PROVIDED)
                )
                .imageUrl(reqEntity.getImageUrl())
                .build();

        return userRepository.save(preparedUser);
    }

    public Object loginViaOAuth2Provider(AuthenticationRequestEntity reqEntity) {
        return null;
    }

    public String login(AuthenticationRequestEntity reqEntity) {
        User loadedUser = this.searchUserByIdentity(reqEntity.getLoginIdentity());

        if (Objects.isNull(loadedUser) || !loadedUser.getHashedPassword().equals(reqEntity.getHashedPassword())) {
            throw new NullPointerException("Invalid username or password");
        }

        return JwtUtils.issueAuthenticatedAccessToken(loadedUser);
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
                    loadedEntity.setAccountProvider(
                            (User.AccountProvider) getOrDefault(reqEntity.getAccountProvider(), User.AccountProvider.SELF_PROVIDED)
                    );

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
