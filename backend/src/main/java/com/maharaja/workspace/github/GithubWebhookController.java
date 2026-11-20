package com.maharaja.workspace.github;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class GithubWebhookController {

    private final GithubSignatureVerifier verifier;
    private final GithubEventService eventService;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public GithubWebhookController(GithubSignatureVerifier verifier, GithubEventService eventService) {
        this.verifier = verifier;
        this.eventService = eventService;
    }

    @PostMapping("/github")
    public Map<String, Object> receive(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody byte[] body) {

        if (!verifier.isValid(body, signature)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature");
        }
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-GitHub-Event header");
        }
        GithubPayload payload;
        try {
            payload = mapper.readValue(body, GithubPayload.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unreadable payload");
        }
        return eventService.handle(event, payload);
    }
}