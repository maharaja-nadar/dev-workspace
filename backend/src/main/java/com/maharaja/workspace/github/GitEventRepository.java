package com.maharaja.workspace.github;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GitEventRepository extends JpaRepository<GitEvent, Long> {
    boolean existsByProjectIdAndTaskKeyAndTypeAndExternalId(
            Long projectId, String taskKey, GitEventType type, String externalId);

    List<GitEvent> findByProjectIdOrderByOccurredAtDesc(Long projectId);
}