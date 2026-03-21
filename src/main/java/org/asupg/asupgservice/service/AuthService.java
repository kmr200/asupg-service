package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.UserDTO;
import org.asupg.asupgservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
            throw new AppException(409, "Конфликт", "Пользователь с именем: " + username + " уже зарегистрирован");
        }

        UserDTO user = new UserDTO(
                username,
                firstName,
                lastName,
                passwordEncoder.encode(password),
                roles
        );

        user = userRepository.save(user);

        return user;
    }

    public UserDTO deleteUser(String username) {
        log.debug("Deleting user {}", username);

        UserDTO user = getUser(username);

        userRepository.delete(user);

        return user;
    }

    public UserDTO getUser(String username) {
        return userRepository.findById(username).orElseThrow(
                () -> new AppException(404, "Ошибка валидации", "Пользователь с именем: " + username + " не найден")
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO updateUser(
            String username,
            String firstName,
            String lastName,
            String password,
            Set<String> roles,
            Boolean locked
    ) {
        UserDTO user = getUser(username);

        if (isNotEmpty(firstName)) user.setFirstName(firstName);
        if (isNotEmpty(lastName)) user.setLastName(lastName);
        if (isNotEmpty(password)) {
            user.setPasswordHash(
                    passwordEncoder.encode(password)
            );
        }
        if (roles != null && !roles.isEmpty()) user.setRoles(roles);
        if (locked != null) user.setLocked(locked);

        return userRepository.save(user);
    }

    private boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }
}
