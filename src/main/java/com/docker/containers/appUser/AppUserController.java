package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUserDTO;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.response.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RepositoryRestController
@ConditionalOnWebApplication
public class AppUserController {
    private final AppUserService service;

    public AppUserController(AppUserService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/appUsers")
    public @ResponseBody ResponseEntity<?> createAppUser(@RequestBody AppUserDTO appUserDto) {
        Result<AppUserDetails> result = service.create(appUserDto);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }

        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("app_user_id", result.getPayload().getAppUserId());

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }
}
