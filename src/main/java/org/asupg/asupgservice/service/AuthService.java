package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.UserDTO;
import org.asupg.asupgservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(
            String username,
            String firstName,
            String lastName,
            String password,
            Set<String> roles
    ) {
        log.debug("Creating user {}", username);

        if (userRepository.existsById(username)) {
            log.warn("User with username {} already exists", username);
            throw new AppException(409, "Validation failed", "User with username: " + username + " already exists");
        }

        UserDTO user = new UserDTO(
                username,
                firstName,
                lastName,
                passwordEncoder.encode(password),
                roles
        );

        user = userRepository.save(user);

        // Hide users password hash
        user.setPasswordHash(null);

        return user;
    }

}
