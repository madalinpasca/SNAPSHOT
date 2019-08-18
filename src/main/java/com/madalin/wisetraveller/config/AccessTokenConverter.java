package com.madalin.wisetraveller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.io.IOException;
import java.util.Map;

public class AccessTokenConverter extends JwtAccessTokenConverter {
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        OAuth2Authentication oAuth2Authentication = super.extractAuthentication(claims);
        Authentication authentication  = oAuth2Authentication.getUserAuthentication() ;
        if (authentication == null)
            return oAuth2Authentication;
        String username = (String)claims.get("name");
        Long id = ((Integer)claims.get("id")).longValue();
        return new OAuth2Authentication(oAuth2Authentication.getOAuth2Request(), new UsernamePasswordAuthenticationToken(
                        WiseTravellerUserDetails.builder()
                                .authorities(authentication.getAuthorities())
                                .username(username)
                                .password(null)
                                .id(id)
                                .build(),
                authentication.getCredentials(),
                authentication.getAuthorities()));
    }
}
