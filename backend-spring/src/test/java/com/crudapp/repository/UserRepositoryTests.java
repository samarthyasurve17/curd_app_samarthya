package com.crudapp.repository;

import com.crudapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void shouldFindUserByUsername() {
        // given
        User user = new User("testuser", passwordEncoder.encode("password"), "test@example.com", "Test User");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void shouldFindUserByEmail() {
        // given
        User user = new User("testuser", passwordEncoder.encode("password"), "test@example.com", "Test User");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldCheckIfUsernameExists() {
        // given
        User user = new User("testuser", passwordEncoder.encode("password"), "test@example.com", "Test User");
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void shouldCheckIfEmailExists() {
        // given
        User user = new User("testuser", passwordEncoder.encode("password"), "test@example.com", "Test User");
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}