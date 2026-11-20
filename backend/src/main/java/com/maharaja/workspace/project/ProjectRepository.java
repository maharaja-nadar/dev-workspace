package com.maharaja.workspace.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByKeyPrefix(String keyPrefix);

    Optional<Project> findByRepoFullNameIgnoreCase(String repoFullName);
}