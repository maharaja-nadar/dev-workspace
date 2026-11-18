package com.maharaja.workspace.task;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank String title,
        String description,
        String assignee) {
}