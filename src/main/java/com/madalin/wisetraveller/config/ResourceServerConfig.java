package com.madalin.wisetraveller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("Resource");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/webjars/**", "/unauthenticated/**").permitAll()
                .antMatchers("/authenticated/**").hasRole("USER")
                .antMatchers("/owner/**").hasRole("OWNER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().denyAll().and().logout().permitAll().and().csrf().disable();
    }
}
