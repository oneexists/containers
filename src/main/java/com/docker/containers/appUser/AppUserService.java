package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUserDTO;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.response.Result;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AppUserService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    Result<AppUserDetails> create(AppUserDTO appUserDto);
}
