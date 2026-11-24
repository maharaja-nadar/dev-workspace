package com.maharaja.workspace.task;

import com.maharaja.workspace.project.ProjectAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final ProjectAccessService access;

    public TaskController(TaskService taskService, ProjectAccessService access) {
        this.taskService = taskService;
        this.access = access;
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request, Authentication auth) {
        access.requireLead(projectId, auth.getName());
        access.requireAssigneeIsMember(projectId, request.assignee());
        return taskService.create(projectId, request);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<Task> list(@PathVariable Long projectId, Authentication auth) {
        access.requireMember(projectId, auth.getName());
        return taskService.listByProject(projectId);
    }

    @GetMapping("/tasks/{id}")
    public Task get(@PathVariable Long id, Authentication auth) {
        Task task = taskService.get(id);
        access.requireMember(task.getProjectId(), auth.getName());
        return task;
    }

    @PutMapping("/tasks/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication auth) {
        Task task = taskService.get(id);
        access.requireLead(task.getProjectId(), auth.getName());
        access.requireAssigneeIsMember(task.getProjectId(), request.assignee());
        return taskService.update(id, request);
    }

    @PatchMapping("/tasks/{id}/status")
    public Task updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request, Authentication auth) {
        Task task = taskService.get(id);
        access.requireMember(task.getProjectId(), auth.getName());
        return taskService.updateStatus(id, request.status());
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        Task task = taskService.get(id);
        access.requireLead(task.getProjectId(), auth.getName());
        taskService.delete(id);
    }
}