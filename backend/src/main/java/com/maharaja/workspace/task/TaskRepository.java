package com.maharaja.workspace.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByIdAsc(Long projectId);

    Optional<Task> findByTaskKey(String taskKey); // used by the webhook on Friday

    void deleteByProjectId(Long projectId);
}