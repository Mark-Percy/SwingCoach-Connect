package com.swingcoach.swingcoach_connect.repository;

import com.swingcoach.swingcoach_connect.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles; // Import this

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// This annotation tells Spring to activate the 'test' profile for this test class.
// It will then pick up application-test.properties.
@ActiveProfiles("test")
// Remove @AutoConfigureTestDatabase(replace = Replace.NONE)
// By default, @DataJpaTest will replace the datasource with an in-memory one
// unless you explicitly tell it not to. We want it to replace it now.
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        // Given: A new User object
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setDateOfBirth(LocalDate.of(1990, 5, 15));
        user.setAccountStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION);
        user.setIsEmailVerified(false);
        user.setVerificationToken("some_token_123");
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        // When: The user is saved to the database
        User savedUser = userRepository.save(user);

        // Then: Verify that the user was saved and has an ID
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getAccountStatus()).isEqualTo(User.AccountStatus.PENDING_EMAIL_VERIFICATION);
        assertThat(savedUser.getIsEmailVerified()).isFalse();

        // Verify that the user can be found by email
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void testFindByEmailNotFound() {
        // When: Searching for an email that does not exist
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then: The Optional should be empty
        assertThat(foundUser).isNotPresent();
    }
}
