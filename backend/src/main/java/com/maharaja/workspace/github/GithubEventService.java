package com.maharaja.workspace.github;

import com.maharaja.workspace.project.Project;
import com.maharaja.workspace.project.ProjectRepository;
import com.maharaja.workspace.task.Task;
import com.maharaja.workspace.task.TaskRepository;
import com.maharaja.workspace.task.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GithubEventService {

    private static final Pattern TASK_KEY = Pattern.compile("\\b[A-Z]{2,5}-\\d+\\b");

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final GitEventRepository eventRepository;

    public GithubEventService(ProjectRepository projectRepository,
            TaskRepository taskRepository,
            GitEventRepository eventRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Map<String, Object> handle(String event, GithubPayload payload) {
        if ("ping".equals(event)) {
            return Map.of("event", "ping", "message", "pong");
        }
        if (payload.repository() == null || payload.repository().fullName() == null) {
            return Map.of("event", event, "ignored", "No repository in payload");
        }
        Optional<Project> project = projectRepository.findByRepoFullNameIgnoreCase(payload.repository().fullName());
        if (project.isEmpty()) {
            return Map.of("event", event, "ignored", "No project is linked to this repository");
        }

        int linked = 0;
        if ("push".equals(event)) {
            linked = handlePush(project.get(), payload);
        } else if ("pull_request".equals(event)) {
            linked = handlePullRequest(project.get(), payload);
        }
        return Map.of("event", event, "linkedEvents", linked);
    }

    private int handlePush(Project project, GithubPayload payload) {
        if (payload.commits() == null) {
            return 0;
        }
        String branch = branchName(payload.ref());
        int count = 0;
        for (GithubPayload.Commit commit : payload.commits()) {
            String author = null;
            if (commit.author() != null) {
                author = commit.author().username() != null ? commit.author().username() : commit.author().name();
            }
            for (String key : findKeys(commit.message(), branch)) {
                if (record(project, key, GitEventType.COMMIT, commit.id(),
                        firstLine(commit.message()), author, commit.url(), branch)) {
                    count++; // commits only record activity
                }
            }
        }
        return count;
    }

    private int handlePullRequest(Project project, GithubPayload payload) {
        GithubPayload.PullRequest pr = payload.pullRequest();
        String action = payload.action();
        if (pr == null || action == null) {
            return 0;
        }

        GitEventType type;
        TaskStatus newStatus = null;
        if (action.equals("opened") || action.equals("reopened") || action.equals("ready_for_review")) {
            type = GitEventType.PR_OPENED;
            newStatus = TaskStatus.IN_REVIEW;
        } else if (action.equals("closed") && pr.merged()) {
            type = GitEventType.PR_MERGED;
            newStatus = TaskStatus.DONE;
        } else if (action.equals("closed")) {
            type = GitEventType.PR_CLOSED;
        } else {
            return 0;
        }

        String branch = pr.head() == null ? null : pr.head().ref();
        String author = pr.user() == null ? null : pr.user().login();
        int count = 0;
        for (String key : findKeys(pr.title(), pr.body(), branch)) {
            if (record(project, key, type, "PR#" + pr.number(), pr.title(), author, pr.htmlUrl(), branch)) {
                applyStatus(key, newStatus);
                count++;
            }
        }
        return count;
    }

    /** Saves the event if the key belongs to a task of this project and is new. */
    private boolean record(Project project, String key, GitEventType type, String externalId,
            String text, String author, String url, String branch) {
        Optional<Task> task = taskRepository.findByTaskKey(key)
                .filter(t -> t.getProjectId().equals(project.getId()));
        if (task.isEmpty() || externalId == null) {
            return false;
        }
        if (eventRepository.existsByProjectIdAndTaskKeyAndTypeAndExternalId(
                project.getId(), key, type, externalId)) {
            return false; // GitHub sent it again
        }
        GitEvent event = new GitEvent();
        event.setProjectId(project.getId());
        event.setTaskKey(key);
        event.setType(type);
        event.setExternalId(externalId);
        event.setMessage(text == null ? null : truncate(text, 500));
        event.setAuthor(author);
        event.setUrl(url);
        event.setBranch(branch);
        eventRepository.save(event);
        return true;
    }

    private void applyStatus(String key, TaskStatus newStatus) {
        if (newStatus == null) {
            return;
        }
        taskRepository.findByTaskKey(key).ifPresent(task -> {
            if (newStatus == TaskStatus.IN_REVIEW && task.getStatus() == TaskStatus.DONE) {
                return; // an old PR reopening must not undo Done
            }
            task.setStatus(newStatus);
            taskRepository.save(task);
        });
    }

    private Set<String> findKeys(String... texts) {
        Set<String> keys = new LinkedHashSet<>();
        for (String text : texts) {
            if (text == null) {
                continue;
            }
            Matcher m = TASK_KEY.matcher(text);
            while (m.find()) {
                keys.add(m.group());
            }
        }
        return keys;
    }

    private String branchName(String ref) {
        return ref == null ? null : ref.replaceFirst("^refs/heads/", "");
    }

    private String firstLine(String text) {
        if (text == null) {
            return null;
        }
        return truncate(text.lines().findFirst().orElse("").trim(), 500);
    }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }
}