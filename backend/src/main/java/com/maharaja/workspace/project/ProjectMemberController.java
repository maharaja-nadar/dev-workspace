package com.maharaja.workspace.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService memberService;
    private final ProjectAccessService access;

    public ProjectMemberController(ProjectMemberService memberService, ProjectAccessService access) {
        this.memberService = memberService;
        this.access = access;
    }

    @GetMapping
    public List<ProjectMember> list(@PathVariable Long projectId, Authentication auth) {
        access.requireMember(projectId, auth.getName());
        return memberService.list(projectId);
    }

    @GetMapping("/candidates")
    public List<String> candidates(@PathVariable Long projectId,
            @RequestParam(defaultValue = "") String q,
            Authentication auth) {
        access.requireLead(projectId, auth.getName());
        return memberService.candidates(projectId, q.trim());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMember add(@PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication auth) {
        access.requireLead(projectId, auth.getName());
        return memberService.add(projectId, request);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long projectId, @PathVariable String username, Authentication auth) {
        access.requireLead(projectId, auth.getName());
        memberService.remove(projectId, username);
    }
}