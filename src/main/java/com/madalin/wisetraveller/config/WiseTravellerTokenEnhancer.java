package com.madalin.wisetraveller.config;

import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;

public class WiseTravellerTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        HashMap<String, Object> additionalInfo = new HashMap<>();
        WiseTravellerUserDetails details = (WiseTravellerUserDetails) authentication.getPrincipal();
        additionalInfo.put("name", details.getUsername());
        additionalInfo.put("id", details.getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
