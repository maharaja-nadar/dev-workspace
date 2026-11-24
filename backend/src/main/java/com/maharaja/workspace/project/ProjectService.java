package com.maharaja.workspace.project;

import com.maharaja.workspace.auth.AppUser;
import com.maharaja.workspace.auth.UserRepository;
import com.maharaja.workspace.task.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
            TaskRepository taskRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    /**
     * A user may create projects if registered as team lead, or if already a lead
     * somewhere.
     */
    public boolean canCreateProjects(String username) {
        boolean flagged = userRepository.findByUsername(username).map(AppUser::isTeamLead).orElse(false);
        boolean leadSomewhere = memberRepository.findByUsername(username).stream()
                .anyMatch(m -> m.getRole() == ProjectRole.LEAD);
        return flagged || leadSomewhere;
    }

    @Transactional
    public Project create(ProjectRequest request, String username) {
        if (!canCreateProjects(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only team leads can create projects");
        }
        String prefix = request.keyPrefix();
        if (prefix == null || !prefix.matches("[A-Z]{2,5}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "keyPrefix must be 2-5 capital letters, e.g. WS");
        }
        if (projectRepository.existsByKeyPrefix(prefix)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "keyPrefix already used");
        }
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setKeyPrefix(prefix);
        Project saved = projectRepository.save(project);

        ProjectMember lead = new ProjectMember();
        lead.setProjectId(saved.getId());
        lead.setUsername(username);
        lead.setRole(ProjectRole.LEAD);
        memberRepository.save(lead);
        return saved;
    }

    public List<Project> findForUser(String username) {
        List<Long> ids = memberRepository.findByUsername(username).stream()
                .map(ProjectMember::getProjectId)
                .toList();
        return projectRepository.findAllById(ids);
    }

    public Project get(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public Project update(Long id, ProjectRequest request) {
        Project project = get(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return projectRepository.save(project);
    }

    public Project linkRepo(Long id, String repoFullName) {
        Project project = get(id);
        project.setRepoFullName(repoFullName);
        return projectRepository.save(project);
    }

    public Project unlinkRepo(Long id) {
        Project project = get(id);
        project.setRepoFullName(null);
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = get(id);
        taskRepository.deleteByProjectId(id);
        memberRepository.deleteByProjectId(id);
        projectRepository.delete(project);
    }
}