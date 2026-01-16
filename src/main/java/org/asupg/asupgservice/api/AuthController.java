package org.asupg.asupgservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.request.LoginRequest;
import org.asupg.asupgservice.model.response.LoginResponse;
import org.asupg.asupgservice.service.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${security.jwt.expireInMinutes:15}")
    private int expireInMinutes;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenGenerator.generateToken(authentication);

            LoginResponse loginResponse = new LoginResponse(
                    token,
                    "Bearer",
                    expireInMinutes
            );

            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.debug("Failed login attempt for user: {}", loginRequest.getUsername());
            throw new AppException(401, "Unauthorized", "Invalid username or password");
        } catch (AuthenticationException e) {
            log.debug("Authentication exception: {}", e.getMessage());
            throw new AppException(401, "Unauthorized", "Authentication Failed");
        }
    }

}
