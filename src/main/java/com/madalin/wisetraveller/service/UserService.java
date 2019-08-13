package com.madalin.wisetraveller.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.madalin.wisetraveller.model.FacebookApiResponse;
import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import com.madalin.wisetraveller.model.enums.TipUser;
import com.madalin.wisetraveller.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.madalin.wisetraveller.service.GoogleIdTokenVerifierBeanWrapper.googleClientId;

@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserService {
    private UserRepository repository;
    private AuthorizationCodeService authorizationCodeService;
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private FacebookAccessTokenApi facebookAccessTokenApi;
    private UserRepository userRepository;

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

    public String getAuthorizationCodeGoogle(String idToken) {
        try {
            GoogleIdToken token = googleIdTokenVerifier.verify(idToken);
            if (token == null ||
                    !token.getPayload().get("aud").toString().equals(googleClientId) ||
                    !token.getPayload().getEmailVerified()) {
                throw new RuntimeException();
            }
            Optional<User> user = userRepository.findByUserProvidedIdAndTipUser(token.getPayload().getSubject(),
                    TipUser.Google);
            user.ifPresent(x -> updateGoogleUser(x, token));
            WiseTravellerUserDetails userDetails = WiseTravellerUserDetailsService.createUserDetails(user.orElseGet(
                    ()-> createGoogleUser(token)));
            OAuth2Authentication authentication = createOAuth2Authentication(userDetails);
            return authorizationCodeService.createAuthorizationCode(authentication);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private User createGoogleUser(GoogleIdToken token) {
        User user = new User();
        String nume = token.getPayload().get("name").toString();
        user.setNume(nume == null ? "User" : nume);
        user.setEmail(token.getPayload().getEmail());
        user.setUsername(user.getEmail());
        user.setUserProvidedId(token.getPayload().getSubject());
        user.setTipUser(TipUser.Google);
        userRepository.save(user);
        return user;
    }

    private void updateGoogleUser(User user, GoogleIdToken token) {
        user.setEmail(token.getPayload().getEmail());
        user.setUsername(user.getEmail());
        String nume = token.getPayload().get("name").toString();
        if (nume != null)
            user.setNume(nume);
        userRepository.save(user);
    }

    public String getAuthorizationCodeFacebook(String accessToken) {
        try {
            FacebookApiResponse response = facebookAccessTokenApi.verify(accessToken);
            Optional<User> user = userRepository.findByUserProvidedIdAndTipUser(response.getId(), TipUser.Facebook);
            user.ifPresent(x -> updateFacebookUser(x, response));
            WiseTravellerUserDetails userDetails = WiseTravellerUserDetailsService.createUserDetails(user.orElseGet(
                    ()-> createFacebookUser(response)));
            OAuth2Authentication authentication = createOAuth2Authentication(userDetails);
            return authorizationCodeService.createAuthorizationCode(authentication);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private User createFacebookUser(FacebookApiResponse response) {
        User user = new User();
        setNamesFacebook(response, user);
        user.setUserProvidedId(response.getId());
        user.setTipUser(TipUser.Facebook);
        userRepository.save(user);
        return user;
    }

    private void setNamesFacebook(FacebookApiResponse response, User user) {
        String nume = response.get("first_name");
        if (nume != null && !nume.trim().isEmpty())
            user.setNume(nume);
        String prenume = response.get("last_name");
        if (prenume != null && !prenume.trim().isEmpty())
            user.setPrenume(prenume);
        if (user.getNume() == null && user.getPrenume() == null)
            user.setNume("User");
        String email = response.get("email");
        String fullname = (nume + " " + prenume).trim();
        user.setEmail(email == null ? fullname.replaceAll("\\s", "").toLowerCase()
                + "@facebook.com" : email);
        user.setUsername(user.getEmail());
        user.setUrlProfil("");
    }

    private void updateFacebookUser(User user, FacebookApiResponse response) {
        setNamesFacebook(response, user);
        userRepository.save(user);
    }

    public List<User> getAll() {
        return repository.findAll();
    }
}
