package com.madalin.wisetraveller.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.madalin.wisetraveller.model.*;
import com.madalin.wisetraveller.model.enums.EmailType;
import com.madalin.wisetraveller.model.enums.FacebookProfilePictureType;
import com.madalin.wisetraveller.model.enums.Role;
import com.madalin.wisetraveller.model.enums.TipUser;
import com.madalin.wisetraveller.repository.ActivationRepository;
import com.madalin.wisetraveller.repository.ResetPasswordRequestRepository;
import com.madalin.wisetraveller.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.madalin.wisetraveller.service.GoogleIdTokenVerifierBeanWrapper.googleClientId;

@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserService {
    private UserRepository repository;
    private AuthorizationCodeService authorizationCodeService;
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private FacebookAccessTokenApi facebookAccessTokenApi;
    private FacebookProfilePictureApi facebookProfilePictureApi;
    private UserRepository userRepository;
    private EmailService emailService;
    private ActivationRepository activationRepository;
    private ResetPasswordRequestRepository resetPasswordRequestRepository;
    private PasswordEncoder userPasswordEncoder;

    private static final String credentialsDefault = "N/A";

    private static OAuth2Authentication createOAuth2Authentication(WiseTravellerUserDetails userDetails) {
        OAuth2Request oAuth2Request = new OAuth2Request(null,
                "Client",
                userDetails.getAuthorities(),
                userDetails.isApproved(),
                new HashSet<>(Arrays.asList("read", "write")),
                Collections.singleton("Resource"),
                null,
                null,
                null
        );
        return new OAuth2Authentication(oAuth2Request, new UsernamePasswordAuthenticationToken(userDetails,
                credentialsDefault,
                userDetails.getAuthorities()));
    }

    public String getAuthorizationCodeGoogle(String idToken, String phoneNumber) {
        try {
                GoogleIdToken token = googleIdTokenVerifier.verify(idToken);
            if (token == null ||
                    !token.getPayload().get("aud").toString().equals(googleClientId) ||
                    !token.getPayload().getEmailVerified()) {
                throw new RuntimeException();
            }
            Optional<User> user = userRepository.findByUserProvidedIdAndTipUser(token.getPayload().getSubject(),
                    TipUser.Google);
            user.ifPresent(x->updateGoogleUser(x, token));
            if (!user.isPresent() && phoneNumber == null) {
                return idToken;
            }
            WiseTravellerUserDetails userDetails = WiseTravellerUserDetailsService.createUserDetails(
                    user.orElseGet(() -> createGoogleUser(token, phoneNumber)), true);
            OAuth2Authentication authentication = createOAuth2Authentication(userDetails);
            return authorizationCodeService.createAuthorizationCode(authentication);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private User createGoogleUser(GoogleIdToken token, String phoneNumber) {
        User user = new User();
        user.setUserRoles(new HashSet<>());
        user.getUserRoles().add(new UserRole(null, Role.User, user));
        user.setUserProvidedId(token.getPayload().getSubject());
        user.setTipUser(TipUser.Google);
        user.setTelefon(phoneNumber);
        updateGoogleUser(user, token);
        userRepository.save(user);
        return user;
    }

    private void updateGoogleUser(User user, GoogleIdToken token) {
        String nume = token.getPayload().get("given_name").toString();
        String prenume = token.getPayload().get("family_name").toString();
        setNames(user, nume, prenume);
        user.setEmail(token.getPayload().getEmail());
        user.setUrlProfil(token.getPayload().get("picture").toString());
        userRepository.save(user);
    }

    public String getAuthorizationCodeFacebook(String accessToken, String phoneNumber) {
        try {
            FacebookApiResponse response = facebookAccessTokenApi.verify(accessToken);
            Optional<User> user = userRepository.findByUserProvidedIdAndTipUser(response.getId(), TipUser.Facebook);
            user.ifPresent(x -> updateFacebookUser(response, x));
            if (!user.isPresent() && phoneNumber == null) {
                return accessToken;
            }
            WiseTravellerUserDetails userDetails = WiseTravellerUserDetailsService.createUserDetails(user.orElseGet(
                    ()-> createFacebookUser(response, phoneNumber)), true);
            OAuth2Authentication authentication = createOAuth2Authentication(userDetails);
            return authorizationCodeService.createAuthorizationCode(authentication);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private User createFacebookUser(FacebookApiResponse response, String phoneNumber) {
        User user = new User();
        user.setUserRoles(new HashSet<>());
        user.getUserRoles().add(new UserRole(null, Role.User, user));
        user.setUserProvidedId(response.getId());
        user.setTipUser(TipUser.Facebook);
        user.setTelefon(phoneNumber);
        updateFacebookUser(response, user);
        return user;
    }

    private void updateFacebookUser(FacebookApiResponse response, User user) {
        String nume = response.get("first_name");
        String prenume = response.get("last_name");
        setNames(user, nume, prenume);
        String email = response.get("email");
        String fullname = (nume + " " + prenume).trim();
        user.setEmail(email == null ? fullname.replaceAll("\\s", "").toLowerCase()
                + "@facebook.com" : email);
        FacebookProfilePictureResponse profile = facebookProfilePictureApi.getProfilePicture(response.getId(),
                FacebookProfilePictureType.Large);
        user.setUrlProfil(profile.getData().getUrl());
        userRepository.save(user);
    }

    private void setNames(User user, String nume, String prenume) {
        if (nume != null && !nume.trim().isEmpty())
            user.setNume(nume);
        if (prenume != null && !prenume.trim().isEmpty())
            user.setPrenume(prenume);
        if (user.getNume() == null && user.getPrenume() == null)
            user.setNume("User");
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public Long register(User user) {
        user.setTipUser(TipUser.Local);
        userRepository.save(user);
        String uuid = UUID.randomUUID().toString();
        if (EmailService.isEmailValid(user.getEmail())) {
            emailService.send(user.getEmail(), new Email.Builder(EmailType.Activation).
                    contentArguments(user.getId(), uuid)
                    .build());
        }
        activationRepository.save(new Activation(null,
                user.getId(),
                uuid,
                LocalDateTime.now().plusDays(30),
                false
        ));
        return user.getId();
    }

    public User getCurrent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WiseTravellerUserDetails details = null;
        if (principal instanceof WiseTravellerUserDetails)
            details = (WiseTravellerUserDetails) principal;
        if (details == null) {
            throw new RuntimeException("No current user");
        }
        return repository.findById(details.getId()).orElseThrow(()-> new RuntimeException("No current user"));
    }

    public String getAvatarPath(User user) {
        return "avatar/"+user.getId();
    }

    public String getAvatarPath(Long id, String extension) {
        User user = repository.findById(id).orElseThrow(()->new RuntimeException("No user with that id"));
        return "avatar/"+user.getId() + "." + extension;
    }

    public void changeAvatarPath(User user, String imageExtension) {
        user.setUrlProfil("http://localhost:8080/unauthenticated/avatar/" +
                user.getId().toString() +
                "." + imageExtension);
        repository.save(user);
    }

    public User get(Long id) {
        return repository.findById(id).orElseThrow(()->new RuntimeException("No user with that id"));
    }

    public void activate(String uuid, Long id) {
        userRepository.findById(id).orElseThrow(()->new RuntimeException("No user with that id"));
        Activation activation = activationRepository.findByUserIdAndUuid(id, uuid).orElseThrow(
                ()->new RuntimeException("Uuid is not valid!")
        );
        activation.setActivated(true);
        activationRepository.save(activation);
    }

    public void resetPassword(String uuid, Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("No user with that id"));
        ResetPasswordRequest request = resetPasswordRequestRepository.findByUserIdAndUuid(id, uuid)
                .orElseThrow(()->new RuntimeException("Uuid is not valid!"));
        user.setParola(request.getPassword());
        resetPasswordRequestRepository.delete(request);
        userRepository.save(user);
    }

    public void resetPassword(String email, String password) {
        User user = userRepository.findByEmailAndTipUser(email, TipUser.Local).orElseThrow(
                ()-> new RuntimeException("No user with that email")
        );
        String uuid = UUID.randomUUID().toString();
        if (EmailService.isEmailValid(user.getEmail())) {
            emailService.send(user.getEmail(), new Email.Builder(EmailType.ResetPassword).
                    contentArguments(user.getId(), uuid)
                    .build());
        }
        ResetPasswordRequest request = resetPasswordRequestRepository.findByUserId(user.getId())
                .orElse(new ResetPasswordRequest());
        request.setUserId(user.getId());
        request.setUuid(uuid);
        request.setPassword(userPasswordEncoder.encode(password));
        resetPasswordRequestRepository.save(request);
    }
}
