package cs.sonu.personalAssiatnt.model;

import java.util.List;

public record AssistantResponse(
        String message,
        List<DocumentInfo> documents) {
}