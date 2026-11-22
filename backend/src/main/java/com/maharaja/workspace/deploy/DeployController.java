package com.maharaja.workspace.deploy;

import com.maharaja.workspace.project.Project;
import com.maharaja.workspace.project.ProjectAccessService;
import com.maharaja.workspace.project.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DeployController {

    private final ProjectRepository projectRepository;
    private final DeploymentRepository deploymentRepository;
    private final ProjectAccessService access;
    private final byte[] deployToken;

    public DeployController(ProjectRepository projectRepository,
            DeploymentRepository deploymentRepository,
            ProjectAccessService access,
            @Value("${app.deploy.token:}") String deployToken) {
        this.projectRepository = projectRepository;
        this.deploymentRepository = deploymentRepository;
        this.access = access;
        this.deployToken = deployToken.getBytes(StandardCharsets.UTF_8);
    }

    // Called by GitHub Actions after a deploy. Protected by a shared token, not a
    // login.
    @PostMapping("/webhooks/deploy")
    public Map<String, Object> record(
            @RequestHeader(value = "X-Deploy-Token", required = false) String token,
            @Valid @RequestBody DeployRequest request) {

        if (deployToken.length == 0 || token == null
                || !MessageDigest.isEqual(deployToken, token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid deploy token");
        }
        Optional<Project> project = projectRepository.findByRepoFullNameIgnoreCase(request.repo());
        if (project.isEmpty()) {
            return Map.of("ignored", "No project is linked to this repository");
        }

        Deployment deployment = new Deployment();
        deployment.setProjectId(project.get().getId());
        deployment.setCommitSha(request.commit());
        deployment.setStatus(request.status());
        deployment.setMessage(firstLine(request.message()));
        deploymentRepository.save(deployment);
        return Map.of("recorded", true);
    }

    @GetMapping("/projects/{projectId}/deployments")
    public List<Deployment> list(@PathVariable Long projectId, Authentication auth) {
        access.requireMember(projectId, auth.getName());
        return deploymentRepository.findByProjectIdOrderByIdDesc(projectId);
    }

    private String firstLine(String text) {
        if (text == null) {
            return null;
        }
        String line = text.lines().findFirst().orElse("").trim();
        return line.length() <= 500 ? line : line.substring(0, 500);
    }
}