package com.maharaja.workspace.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectIdOrderByIdAsc(Long projectId);

    List<ProjectMember> findByUsername(String username);

    Optional<ProjectMember> findByProjectIdAndUsername(Long projectId, String username);

    boolean existsByProjectIdAndUsername(Long projectId, String username);

    void deleteByProjectId(Long projectId);
}