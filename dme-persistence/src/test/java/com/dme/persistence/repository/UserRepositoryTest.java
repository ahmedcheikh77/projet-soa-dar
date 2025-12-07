package com.dme.persistence.repository;

import com.dme.persistence.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hash")
                .fullName("Test User")
                .role(com.dme.persistence.entity.UserRole.PATIENT)
                .active(true)
                .build();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    public void testFindByEmail() {
        User user = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .passwordHash("hash")
                .fullName("Test User 2")
                .role(com.dme.persistence.entity.UserRole.DOCTOR)
                .active(true)
                .build();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test2@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test2@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByActiveTrue() {
        User activeUser = User.builder()
                .username("activeuser")
                .email("active@example.com")
                .passwordHash("hash")
                .fullName("Active User")
                .role(com.dme.persistence.entity.UserRole.PATIENT)
                .active(true)
                .build();

        userRepository.save(activeUser);

        assertTrue(userRepository.findByActiveTrue().size() > 0);
    }
}
