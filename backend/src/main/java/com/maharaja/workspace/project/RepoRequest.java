package com.maharaja.workspace.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RepoRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", message = "use the form owner/repository") String repoFullName) {
}