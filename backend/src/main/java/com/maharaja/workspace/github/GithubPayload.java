package com.maharaja.workspace.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubPayload(
        String action,
        String ref,
        Repository repository,
        List<Commit> commits,
        @JsonProperty("pull_request") PullRequest pullRequest) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(@JsonProperty("full_name") String fullName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Commit(String id, String message, String url, Author author) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(String name, String username) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PullRequest(
            int number,
            String title,
            String body,
            boolean merged,
            @JsonProperty("html_url") String htmlUrl,
            User user,
            Head head) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(String login) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Head(String ref) {
    }
}