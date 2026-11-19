package com.maharaja.workspace.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) @Pattern(regexp = "[A-Za-z0-9_.-]+", message = "username may contain letters, numbers, _ . and -") String username,

        @NotBlank @Size(min = 8, max = 72, message = "password must be 8 to 72 characters") String password,

        String githubUsername) {
}