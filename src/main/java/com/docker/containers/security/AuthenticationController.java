package com.docker.containers.security;

import com.docker.containers.appUser.AppUserService;
import com.docker.containers.appUser.models.AppUserDTO;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.response.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@ConditionalOnWebApplication
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtConverter converter;
    private final AppUserService service;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtConverter converter, AppUserService service) {
        this.authenticationManager = authenticationManager;
        this.converter = converter;
        this.service = service;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody AppUserDTO appUserDto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(appUserDto.username(), appUserDto.password());

        try {
            Authentication authentication = authenticationManager.authenticate(token);

            if (authentication.isAuthenticated()) {
                String jwtToken = converter.getTokenFromUser((AppUserDetails) authentication.getPrincipal());
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("jwt_token", jwtToken);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@AuthenticationPrincipal AppUserDetails appUser) {
        String jwt = converter.getTokenFromUser(appUser);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("jwt_token", jwt);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}