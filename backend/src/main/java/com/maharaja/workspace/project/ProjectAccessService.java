package com.maharaja.workspace.project;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectAccessService {

    private final ProjectMemberRepository memberRepository;

    public ProjectAccessService(ProjectMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public ProjectMember requireMember(Long projectId, String username) {
        return memberRepository.findByProjectIdAndUsername(projectId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You are not a member of this project"));
    }

    public void requireLead(Long projectId, String username) {
        if (requireMember(projectId, username).getRole() != ProjectRole.LEAD) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a project lead can do this");
        }
    }

    public void requireAssigneeIsMember(Long projectId, String assignee) {
        if (assignee == null || assignee.isBlank()) {
            return;
        }
        if (!memberRepository.existsByProjectIdAndUsername(projectId, assignee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Assignee must be a member of the project");
        }
    }
}