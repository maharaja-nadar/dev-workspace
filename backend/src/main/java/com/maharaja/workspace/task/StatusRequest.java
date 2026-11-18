package com.maharaja.workspace.task;

import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull TaskStatus status) {
}