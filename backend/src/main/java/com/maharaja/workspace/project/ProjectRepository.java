package com.maharaja.workspace.project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByKeyPrefix(String keyPrefix);
}