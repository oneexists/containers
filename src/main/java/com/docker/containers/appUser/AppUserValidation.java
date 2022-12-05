package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.response.ActionStatus;
import com.docker.containers.response.Result;
import org.springframework.stereotype.Component;

@Component
public class AppUserValidation {
    public Result<AppUserDetails> validate(String username, String password) {
        Result<AppUserDetails> result = new Result<>();

        if (isBlankString(username)) {
            result.addMessage(ActionStatus.INVALID, "username is required");
        }
        if (isBlankString(password)) {
            result.addMessage(ActionStatus.INVALID, "password is required");
        }

        return result;
    }

    public boolean isBlankString(String string ) {
        return string == null || string.isBlank();
    }
}
