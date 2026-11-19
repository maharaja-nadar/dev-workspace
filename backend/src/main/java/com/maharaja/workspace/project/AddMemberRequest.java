package com.maharaja.workspace.project;

import jakarta.validation.constraints.NotBlank;

public record AddMemberRequest(
        @NotBlank String username,
        ProjectRole role) {
}