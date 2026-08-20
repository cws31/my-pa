package cs.sonu.personalAssiatnt.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

public interface DocumentAssistant {

    @SystemMessage("""
            You are a personal document assistant.

            Your job is to help the user find their documents.

            When the user asks for a document, use the available document tools.

            Do not invent documents.

            After receiving the tool result, clearly tell the user what was found.
            """)
    String chat(@UserMessage String userMessage);
}