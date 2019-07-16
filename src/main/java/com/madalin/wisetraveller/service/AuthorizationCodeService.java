package com.madalin.wisetraveller.service;

import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class AuthorizationCodeService implements AuthorizationCodeServices {

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AuthorizationCodeServiceKey {
        private LocalDateTime expiration;
        private String code;
    }

    private Map<String, OAuth2Authentication> codeAuthenticationMap = new ConcurrentHashMap<>();
    private Map<Long, AuthorizationCodeServiceKey> idCodeMap = new ConcurrentHashMap<>();
    private Lock lock = new ReentrantLock();

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        WiseTravellerUserDetails userDetails = (WiseTravellerUserDetails)
                authentication.getUserAuthentication().getPrincipal();
        AuthorizationCodeServiceKey newKey = new AuthorizationCodeServiceKey(LocalDateTime.now().plusMinutes(1),
                UUID.randomUUID().toString());
        lock.lock();
        AuthorizationCodeServiceKey key = idCodeMap.get(userDetails.getId());
        if (key == null || key.expiration.isBefore(LocalDateTime.now())) {
            idCodeMap.put(userDetails.getId(), newKey);
            codeAuthenticationMap.put(newKey.getCode(), authentication);
            lock.unlock();
            return newKey.getCode();
        }
        codeAuthenticationMap.put(key.getCode(), authentication);
        lock.unlock();
        return key.getCode();
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        lock.lock();
        OAuth2Authentication authentication = codeAuthenticationMap.get(code);
        if (authentication == null) {
            lock.unlock();
            return null;
        }
        WiseTravellerUserDetails userDetails = (WiseTravellerUserDetails) authentication.getUserAuthentication().getPrincipal();
        AuthorizationCodeServiceKey key = idCodeMap.get(userDetails.getId());
        lock.unlock();
        if (key == null || key.expiration.isBefore(LocalDateTime.now())) {
            return null;
        }
        return authentication;
    }
}
