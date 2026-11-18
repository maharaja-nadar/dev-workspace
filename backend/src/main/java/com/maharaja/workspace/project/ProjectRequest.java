package com.maharaja.workspace.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank String name,
        String description,
        String keyPrefix) {
}