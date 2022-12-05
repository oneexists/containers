package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUser;
import com.docker.containers.appUser.models.AppUserDTO;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.appUser.models.AppUserRole;
import com.docker.containers.response.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppUserServiceImplTest {
    AppUserService service;
    @MockBean
    AppUserRepository repository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @Autowired
    AppUserValidation validation;

    @BeforeEach
    void setUp() {
        service = new AppUserServiceImpl(repository, passwordEncoder, validation);
    }

    /**
     * Test method for {@link com.docker.containers.appUser.AppUserServiceImpl#loadUserByUsername(java.lang.String)}.
     */
    @Test
    void testLoadUserByUsername() {
        AppUser expected = new AppUser("test username", "P@ssw0rd!", AppUserRole.USER);
        expected.setAppUserId(2L);
        given(repository.findByUsername(expected.getUsername())).willReturn(Optional.of(expected));
        ArgumentCaptor<String> loadArgCaptor = ArgumentCaptor.forClass(String.class);

        service.loadUserByUsername(expected.getUsername());
        verify(repository).findByUsername(loadArgCaptor.capture());

        assertThat(loadArgCaptor.getValue()).isEqualTo(expected.getUsername());
    }

    @Test
    void testShouldNotLoadMissingUserByUsername() {
        String input = "missing username";
        given(repository.findByUsername(input)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(input)).isInstanceOf(UsernameNotFoundException.class);
    }

    /**
     * Test method for {@link com.docker.containers.appUser.AppUserServiceImpl#create(com.docker.containers.appUser.models.AppUserDTO)}.
     */
    @Test
    void testShouldCreate() {
        AppUserDTO appUserDto = new AppUserDTO("test username", "P@ssw0rd!");
        AppUser expected = new AppUser("test username", "P@ssw0rd!", AppUserRole.USER);
        expected.setAppUserId(2L);
        given(passwordEncoder.encode(any(String.class))).willReturn(expected.getPassword());
        given(repository.save(any(AppUser.class))).willReturn(expected);
        ArgumentCaptor<AppUser> createArgCaptor = ArgumentCaptor.forClass(AppUser.class);

        service.create(appUserDto);
        verify(repository).save(createArgCaptor.capture());

        assertThat(createArgCaptor.getValue().getUsername()).isEqualTo(expected.getUsername());
        assertThat(createArgCaptor.getValue().getPassword()).isEqualTo(expected.getPassword());
    }

    @Test
    void testShouldNotCreateNullUsername() {
        AppUserDTO appUserDto = new AppUserDTO(null, "P@ssw0rd!");

        Result<AppUserDetails> result = service.create(appUserDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("username is required"));
    }

    @Test
    void testShouldNotCreateEmptyUsername() {
        AppUserDTO appUserDto = new AppUserDTO("\t", "P@ssw0rd!");

        Result<AppUserDetails> result = service.create(appUserDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("username is required"));
    }

    @Test
    void testShouldNotCreateNullPassword() {
        AppUserDTO appUserDto = new AppUserDTO("test username", null);

        Result<AppUserDetails> result = service.create(appUserDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("password is required"));
    }

    @Test
    void testShouldNotCreateEmptyPassword() {
        AppUserDTO appUserDto = new AppUserDTO("test username", "   ");

        Result<AppUserDetails> result = service.create(appUserDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("password is required"));
    }

    @Test
    void testShouldNotCreateDuplicateUsername() {
        given(repository.save(any(AppUser.class))).willThrow(DataIntegrityViolationException.class);
        AppUserDTO appUserDto = new AppUserDTO("test username", "P@ssw0rd!");

        Result<AppUserDetails> result = service.create(appUserDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("provided username already exists"));
    }
}
