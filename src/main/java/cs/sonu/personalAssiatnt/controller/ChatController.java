package cs.sonu.personalAssiatnt.controller;

import org.springframework.web.bind.annotation.*;

import cs.sonu.personalAssiatnt.assistant.DocumentAssistant;
import cs.sonu.personalAssiatnt.model.ChatRequest;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final DocumentAssistant documentAssistant;

    public ChatController(DocumentAssistant documentAssistant) {
        this.documentAssistant = documentAssistant;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {

        System.out.println(
                "CONTROLLER -> " + request.message());

        String response = documentAssistant.chat(request.message());

        System.out.println(
                "ASSISTANT -> " + response);

        return response;
    }
}