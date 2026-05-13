package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.UserEntity;
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

    public UserEntity registerUser(
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

        UserEntity userEntity = new UserEntity(
                username,
                firstName,
                lastName,
                passwordEncoder.encode(password),
                roles
        );

        userEntity = userRepository.save(userEntity);

        return userEntity;
    }

    public UserEntity deleteUser(String username) {
        log.debug("Deleting user {}", username);

        UserEntity userEntity = getUser(username);

        userRepository.delete(userEntity);

        return userEntity;
    }

    public UserEntity getUser(String username) {
        return userRepository.findById(username).orElseThrow(
                () -> new AppException(404, "Ошибка валидации", "Пользователь с именем: " + username + " не найден")
        );
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity updateUser(
            String username,
            String firstName,
            String lastName,
            String password,
            Set<String> roles,
            Boolean locked
    ) {
        UserEntity userEntity = getUser(username);

        if (isNotEmpty(firstName)) userEntity.setFirstName(firstName);
        if (isNotEmpty(lastName)) userEntity.setLastName(lastName);
        if (isNotEmpty(password)) {
            userEntity.setPasswordHash(
                    passwordEncoder.encode(password)
            );
        }
        if (roles != null && !roles.isEmpty()) userEntity.setRoles(roles);
        if (locked != null) userEntity.setLocked(locked);

        return userRepository.save(userEntity);
    }

    private boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }
}
