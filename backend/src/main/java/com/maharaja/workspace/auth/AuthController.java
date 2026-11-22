package com.maharaja.workspace.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("username", authentication.getName());
    }

    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        return authService.profile(authentication.getName());
    }

    @PutMapping("/profile/github")
    public ProfileResponse updateGithub(@Valid @RequestBody GithubUsernameRequest request,
            Authentication authentication) {
        return authService.updateGithub(authentication.getName(), request.githubUsername());
    }

    @PutMapping("/profile/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
    }
}