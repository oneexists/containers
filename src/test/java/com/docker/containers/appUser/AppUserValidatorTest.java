package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUser;
import com.docker.containers.appUser.models.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppUserValidatorTest {
    String USERNAME_FIELD = "username";
    String PASSWORD_FIELD = "password";
    @Autowired
    AppUserValidator validator;
    Errors errors;
    AppUser input;

    @BeforeEach
    void setUp() {
        input = new AppUser("username", "P@ssw0rd!", AppUserRole.USER);
        errors = new BeanPropertyBindingResult(input, "input");
    }

    @Test
    void testShouldValidate() {
        validator.validate(input, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void testEmptyUsernameShouldNotValidate() {
        input.setUsername("\t");

        validator.validate(input, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(USERNAME_FIELD));
    }

    @Test
    void testNullNameShouldNotValidate() {
        input.setUsername(null);

        validator.validate(input, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(USERNAME_FIELD));
    }

    @Test
    void testEmptyPasswordShouldNotValidate() {
        input.setPassword("   ");

        validator.validate(input, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(PASSWORD_FIELD));
    }

    @Test
    void testNullPasswordShouldNotValidate() {
        input.setPassword(null);

        validator.validate(input, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(PASSWORD_FIELD));
    }
}
