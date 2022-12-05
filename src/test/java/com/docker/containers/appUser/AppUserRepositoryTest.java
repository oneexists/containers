package com.docker.containers.appUser;

import com.docker.containers.appUser.models.AppUser;
import com.docker.containers.appUser.models.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {
    @Autowired
    AppUserRepository repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("call set_known_good_state();");
    }

    @Test
    void testShouldFindById() {
        AppUser expected = new AppUser("username", "$2a$10$bJ.Q1/9A/1i4LpO90CVnHO.DK464jvQnrXUo0QHJggWEhgLF3eElm", AppUserRole.USER);

        AppUser actual = repository.findById(1L).orElse(null);

        assertNotNull(actual);
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    void testShouldFindByUsername() {
        AppUser expected = new AppUser("username", "$2a$10$bJ.Q1/9A/1i4LpO90CVnHO.DK464jvQnrXUo0QHJggWEhgLF3eElm", AppUserRole.USER);

        AppUser actual = repository.findByUsername("username").orElse(null);

        assertNotNull(actual);
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getUserRole(), actual.getUserRole());
        assertEquals(expected.isAccountNonExpired(), actual.isAccountNonExpired());
        assertEquals(expected.isAccountNonLocked(), actual.isAccountNonLocked());
        assertEquals(expected.isCredentialsNonExpired(), actual.isCredentialsNonExpired());
        assertEquals(expected.isEnabled(), actual.isEnabled());
    }

    @Test
    void testShouldNotFindByUsername() {
        AppUser actual = repository.findByUsername("not a username").orElse(null);

        assertNull(actual);
    }

    @Test
    void testShouldNotCreateDuplicateUsername() {
        AppUser duplicate = new AppUser("username", "$2a$10$bJ.Q1/9A/1i4LpO90CVnHO.DK464jvQnrXUo0QHJggWEhgLF3eElm", AppUserRole.USER);

        assertThatThrownBy(() -> repository.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
