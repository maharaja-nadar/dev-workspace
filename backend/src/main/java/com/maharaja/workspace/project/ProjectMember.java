package com.maharaja.workspace.project;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "username"}))
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role = ProjectRole.DEVELOPER;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public ProjectRole getRole() { return role; }
    public void setRole(ProjectRole role) { this.role = role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}