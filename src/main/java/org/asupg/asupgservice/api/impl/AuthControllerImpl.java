package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.api.AuthController;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.UserDTO;
import org.asupg.asupgservice.model.request.LoginRequest;
import org.asupg.asupgservice.model.request.RegisterUserRequest;
import org.asupg.asupgservice.model.request.UpdateUserRequest;
import org.asupg.asupgservice.model.response.LoginResponse;
import org.asupg.asupgservice.service.AuthService;
import org.asupg.asupgservice.service.JwtService;
import org.asupg.asupgservice.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @Value("${security.jwt.expireInMinutes:15}")
    private int expireInMinutes;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Validated @RequestBody LoginRequest loginRequest
    ) {
        String username = loginRequest.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            throw new AppException(429, "Too Many Requests",
                    "Аккаунт заблокирован из-за превышения количества попыток входа. Попробуйте снова через "
                            + loginAttemptService.lockDurationMinutes
                            + " минут.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            loginAttemptService.recordSuccess(loginRequest.getUsername());
            String token = jwtService.generateToken(authentication);

            LoginResponse loginResponse = new LoginResponse(
                    token,
                    "Bearer",
                    expireInMinutes
            );

            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            loginAttemptService.recordFailure(username);
            int remaining = loginAttemptService.getRemainingAttempts(username);
            log.debug("Failed login attempt for user: {}, remaining attempts: {}", username, remaining);
            throw new AppException(401, "Unauthorized",
                    remaining > 0
                            ? "Неверный логин или пароль. Осталось попыток: " + remaining
                            : "Аккаунт заблокирован. Попробуйте снова через 15 минут."
            );
        } catch (AuthenticationException e) {
            log.debug("Authentication exception: {}", e.getMessage());
            throw new AppException(401, "Unauthorized", "Authentication Failed");
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> register(
            @Validated @RequestBody RegisterUserRequest registerUserRequest
    ) {
        UserDTO user = authService.registerUser(
                registerUserRequest.getUsername(),
                registerUserRequest.getFirstName(),
                registerUserRequest.getLastName(),
                registerUserRequest.getPassword(),
                registerUserRequest.getRoles()
        );

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable String username
    ) {
        UserDTO user = authService.getUser(username);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deleteUser(
            @PathVariable String username
    ) {
        UserDTO user = authService.deleteUser(username);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String username,
            @Validated @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserDTO user = authService.updateUser(
                username,
                updateUserRequest.getFirstName(),
                updateUserRequest.getLastName(),
                updateUserRequest.getPassword(),
                updateUserRequest.getRoles(),
                updateUserRequest.getLocked()
        );

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
