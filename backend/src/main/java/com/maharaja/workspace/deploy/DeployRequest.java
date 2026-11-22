package com.maharaja.workspace.deploy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeployRequest(
        @NotBlank String repo,
        @NotBlank String commit,
        @NotNull DeploymentStatus status,
        String message) {
}