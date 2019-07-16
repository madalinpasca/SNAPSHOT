package com.madalin.wisetraveller.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FacebookAccessTokenApiBeanWrapper {
    private FacebookAccessTokenApi accessTokenApi = null;

    @Bean
    public FacebookAccessTokenApi facebookAccessTokenApi() {
        if (accessTokenApi == null) {
            accessTokenApi = new FacebookAccessTokenApi.Builder()
                    .setMajorVersion(3)
                    .setMinorVersion(3)
                    .setFields(Arrays.asList("id", "email", "name"))
                    .build();
        }
        return accessTokenApi;
    }
}
