package cs.sonu.personalAssiatnt.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import cs.sonu.personalAssiatnt.tools.DocumentTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cs.sonu.personalAssiatnt.assistant.DocumentAssistant;

@Configuration
public class AiConfig {

    @Value("${gemini.api.key}")
    private String key;

    @Bean
    public ChatModel chatModel() {

        return GoogleAiGeminiChatModel.builder()
                .apiKey(key)
                .modelName("gemini-3.5-flash-lite")
                .returnThinking(true)
                .sendThinking(true)
                .temperature(0.1)
                .build();
    }

    @Bean
    public DocumentAssistant documentAssistant(ChatModel chatModel, DocumentTools documentTools) {
        return AiServices.builder(DocumentAssistant.class)
                .chatModel(chatModel)
                .tools(documentTools)
                .build();

    }
}