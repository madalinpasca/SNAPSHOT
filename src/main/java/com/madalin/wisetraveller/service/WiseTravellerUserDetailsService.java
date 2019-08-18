package com.madalin.wisetraveller.service;

import com.madalin.wisetraveller.model.Activation;
import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.WiseTravellerUserDetails;
import com.madalin.wisetraveller.model.enums.TipUser;
import com.madalin.wisetraveller.repository.ActivationRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class WiseTravellerUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    private ActivationRepository activationRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndTipUser(username, TipUser.Local).orElseThrow(()->
                        new UsernameNotFoundException("No user with that email"));
        Optional<Activation> activation = activationRepository.findByUserId(user.getId());
        return createUserDetails(user,activation.isPresent() && activation.get().isActivated());
    }

    static WiseTravellerUserDetails createUserDetails(User user, boolean enabled) {
        if (user == null)
            throw new NullPointerException();
        return WiseTravellerUserDetails.builder()
                .authorities(user.getUserRoles().stream().map((x)->x.getRole().toAuthority()).collect(Collectors.toList()))
                .password(user.getParola())
                .username(user.getEmail())
                .id(user.getId())
                .enabled(enabled)
                .build();
    }
}
