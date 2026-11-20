package com.maharaja.workspace.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectAccessService access;

    public ProjectController(ProjectService projectService, ProjectAccessService access) {
        this.projectService = projectService;
        this.access = access;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@Valid @RequestBody ProjectRequest request, Authentication auth) {
        return projectService.create(request, auth.getName());
    }

    @GetMapping
    public List<Project> list(Authentication auth) {
        return projectService.findForUser(auth.getName());
    }

    @GetMapping("/{id}")
    public Project get(@PathVariable Long id, Authentication auth) {
        access.requireMember(id, auth.getName());
        return projectService.get(id);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication auth) {
        access.requireLead(id, auth.getName());
        return projectService.update(id, request);
    }

    @PutMapping("/{id}/repo")
    public Project linkRepo(@PathVariable Long id, @Valid @RequestBody RepoRequest request, Authentication auth) {
        access.requireLead(id, auth.getName());
        return projectService.linkRepo(id, request.repoFullName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        access.requireLead(id, auth.getName());
        projectService.delete(id);
    }
}