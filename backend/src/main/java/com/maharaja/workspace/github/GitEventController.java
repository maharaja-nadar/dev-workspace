package com.maharaja.workspace.github;

import com.maharaja.workspace.project.ProjectAccessService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/events")
public class GitEventController {

    private final GitEventRepository eventRepository;
    private final ProjectAccessService access;

    public GitEventController(GitEventRepository eventRepository, ProjectAccessService access) {
        this.eventRepository = eventRepository;
        this.access = access;
    }

    @GetMapping
    public List<GitEvent> list(@PathVariable Long projectId, Authentication auth) {
        access.requireMember(projectId, auth.getName());
        return eventRepository.findByProjectIdOrderByOccurredAtDesc(projectId);
    }
}