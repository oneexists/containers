package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUser;
import com.docker.containers.appUser.models.AppUserDTO;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.appUser.models.AppUserRole;
import com.docker.containers.response.ActionStatus;
import com.docker.containers.response.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService, UserDetailsService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserValidation validation;

    public AppUserServiceImpl(AppUserRepository repository, PasswordEncoder passwordEncoder, AppUserValidation validation) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.validation = validation;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repository.findByUsername(username).orElse(null);

        if (appUser == null || !appUser.isEnabled()) {
            throw new UsernameNotFoundException(username + " not found");
        }

        return new AppUserDetails(appUser);
    }

    @Override
    public Result<AppUserDetails> create(AppUserDTO appUserDto) {
        Result<AppUserDetails> result = validation.validate(appUserDto.username(), appUserDto.password());
        if (!result.isSuccess()) {
            return result;
        }

        String password = passwordEncoder.encode(appUserDto.password());
        AppUser appUser = new AppUser(appUserDto.username(), password, AppUserRole.USER);

        try {
            appUser = repository.save(appUser);
            result.setPayload(new AppUserDetails(appUser));
        } catch (DataIntegrityViolationException exception) {
            result.addMessage(ActionStatus.INVALID, "the provided username already exists");
        }

        return result;
    }
}
