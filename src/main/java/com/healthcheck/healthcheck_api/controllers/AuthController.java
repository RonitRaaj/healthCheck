package com.healthcheck.healthcheck_api.controllers;

import com.healthcheck.healthcheck_api.dto.LoginRequest;
import com.healthcheck.healthcheck_api.dto.LoginResponse;
import com.healthcheck.healthcheck_api.dto.SignupRequest;
import com.healthcheck.healthcheck_api.dto.UserResponse;
import com.healthcheck.healthcheck_api.models.User;
import com.healthcheck.healthcheck_api.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequest request) {

        if (userRepository
                .findByUsername(request.getUsername())
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        securityContextRepository.saveContext(
                SecurityContextHolder.getContext(),
                httpRequest,
                httpResponse
        );

        return ResponseEntity.ok(
                new LoginResponse(
                        "Login successful",
                        authentication.getName()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null) {

            new SecurityContextLogoutHandler()
                    .logout(
                            request,
                            response,
                            authentication
                    );
        }

        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            Authentication authentication) {

        return ResponseEntity.ok(
                new UserResponse(
                        authentication.getName()
                )
        );
    }
}