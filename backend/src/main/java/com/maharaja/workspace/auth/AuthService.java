package com.maharaja.workspace.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setGithubUsername(request.githubUsername());
        userRepository.save(user);
        return new AuthResponse(createToken(user.getUsername()), user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password"));
        return new AuthResponse(createToken(user.getUsername()), user.getUsername());
    }

    private String createToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plus(8, ChronoUnit.HOURS))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public ProfileResponse profile(String username) {
        AppUser user = find(username);
        return new ProfileResponse(user.getUsername(), user.getGithubUsername());
    }

    public ProfileResponse updateGithub(String username, String githubUsername) {
        AppUser user = find(username);
        user.setGithubUsername(githubUsername == null || githubUsername.isBlank() ? null : githubUsername.trim());
        userRepository.save(user);
        return new ProfileResponse(user.getUsername(), user.getGithubUsername());
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        AppUser user = find(username);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is wrong");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private AppUser find(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}