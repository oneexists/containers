package com.docker.containers.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static com.docker.containers.appUser.models.AppUserPermission.USER_READ;
import static com.docker.containers.appUser.models.AppUserPermission.USER_WRITE;

@Configuration
@ConditionalOnWebApplication
public class SecurityConfig {
    private final JwtConverter converter;

    public SecurityConfig(JwtConverter converter) {
        this.converter = converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        http.csrf().disable();
        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                    .requestMatchers("/authenticate", "/").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/appUsers").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/appUsers").hasAuthority(USER_READ.getPermission())
                    .requestMatchers(HttpMethod.PUT, "/api/appUsers").hasAuthority(USER_WRITE.getPermission())
                    .requestMatchers(HttpMethod.DELETE, "/api/appUsers").hasAuthority(USER_WRITE.getPermission())
                .anyRequest().authenticated();

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
        http.addFilter(new JwtRequestFilter(authenticationManager(authConfig), converter));

        return http.build();
    }

    @Bean
    JwtAuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    JwtAccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
