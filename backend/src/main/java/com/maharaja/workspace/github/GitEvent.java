package com.maharaja.workspace.github;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "git_event", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id", "task_key", "type",
        "external_id" }))
public class GitEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "task_key", nullable = false)
    private String taskKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GitEventType type;

    @Column(name = "external_id", nullable = false)
    private String externalId; // commit id, or PR#7

    @Column(length = 500)
    private String message;

    private String author;

    @Column(length = 500)
    private String url;

    private String branch;

    private LocalDateTime occurredAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public GitEventType getType() {
        return type;
    }

    public void setType(GitEventType type) {
        this.type = type;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}