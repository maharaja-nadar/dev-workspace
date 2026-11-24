package com.maharaja.workspace.project;

import com.maharaja.workspace.auth.AppUser;
import com.maharaja.workspace.auth.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(ProjectMemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectMember> list(Long projectId) {
        return memberRepository.findByProjectIdOrderByIdAsc(projectId);
    }

    /** Usernames starting with the prefix that are not yet in this project. */
    public List<String> candidates(Long projectId, String prefix) {
        Set<String> existing = memberRepository.findByProjectIdOrderByIdAsc(projectId).stream()
                .map(ProjectMember::getUsername)
                .collect(Collectors.toSet());
        return userRepository.findTop50ByUsernameStartingWithIgnoreCaseOrderByUsernameAsc(prefix).stream()
                .map(AppUser::getUsername)
                .filter(name -> !existing.contains(name))
                .limit(20)
                .toList();
    }

    public ProjectMember add(Long projectId, AddMemberRequest request) {
        if (!userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username");
        }
        if (memberRepository.existsByProjectIdAndUsername(projectId, request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member");
        }
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUsername(request.username());
        member.setRole(request.role() != null ? request.role() : ProjectRole.DEVELOPER);
        return memberRepository.save(member);
    }

    public void remove(Long projectId, String username) {
        ProjectMember member = memberRepository.findByProjectIdAndUsername(projectId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        if (member.getRole() == ProjectRole.LEAD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lead cannot be removed");
        }
        memberRepository.delete(member);
    }
}