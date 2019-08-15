package com.madalin.wisetraveller.service;

import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import com.madalin.wisetraveller.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class WiseTravellerUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()->
                        new UsernameNotFoundException("No user with that email"));
        return createUserDetails(user);
    }

    public static WiseTravellerUserDetails createUserDetails(User user) {
        if (user == null)
            throw new NullPointerException();
        return WiseTravellerUserDetails.builder()
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
                .password(user.getParola())
                .username(user.getEmail())
                .id(user.getId())
                .build();
    }
}
