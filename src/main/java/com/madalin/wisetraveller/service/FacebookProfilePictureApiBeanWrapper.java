package com.madalin.wisetraveller.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class FacebookProfilePictureApiBeanWrapper {
    private FacebookProfilePictureApi profilePictureApi;

    @Bean
    public FacebookProfilePictureApi facebookProfilePictureApi() {
        if (profilePictureApi == null) {
            profilePictureApi = new FacebookProfilePictureApi();
        }
        return profilePictureApi;
    }
}
