package com.madalin.wisetraveller.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleIdTokenVerifierBeanWrapper {
    private GoogleIdTokenVerifier googleIdTokenVerifier = null;

    @SuppressWarnings("WeakerAccess")
    public static final String googleClientId = "216644568526-rfnee6msqb997vnblcba67mj1auprkaq.apps.googleusercontent.com";
    private static final List<String> issuers = Arrays.asList("accounts.google.com", "https://accounts.google.com");
    @Bean
    GoogleIdTokenVerifier googleIdTokenVerifier() {
        if (googleIdTokenVerifier == null) {
            googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singleton(googleClientId))
                    .setIssuers(issuers).build();
        }
        return googleIdTokenVerifier;
    }
}
