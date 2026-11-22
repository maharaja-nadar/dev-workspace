package com.maharaja.workspace.auth;

import jakarta.validation.constraints.Pattern;

public record GithubUsernameRequest(
        @Pattern(regexp = "^$|^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$", message = "not a valid GitHub username") String githubUsername) {
}