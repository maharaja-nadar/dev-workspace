package com.maharaja.workspace.deploy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByProjectIdOrderByIdDesc(Long projectId);
}