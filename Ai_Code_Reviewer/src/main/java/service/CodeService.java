package service;

import model.CodeRequest;
import model.CodeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CodeService {


    public CodeResponse reviewCode(CodeRequest request) {

        // ✅ Validation
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Code cannot be empty");
        }

        // ✅ Strong Prompt
        String prompt = "You are a senior software engineer.\n\n" +
                "Analyze the following " + request.getLanguage() + " code.\n\n" +
                "IMPORTANT: Return ONLY valid JSON. No explanation. No text.\n\n" +
                "Format:\n" +
                "{\n" +
                "  \"bugs\": [\"...\"],\n" +
                "  \"improvements\": [\"...\"],\n" +
                "  \"score\": 0,\n" +
                "  \"summary\": \"...\",\n" +
                "  \"fixed_code\": \"...\"\n" +
                "}\n\n" +
                "Code:\n" + request.getCode();

        String aiResponse = callAI(prompt);

        try {
            String clean = aiResponse.replace("```json", "").replace("```", "");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(clean, CodeResponse.class);

        } catch (Exception e) {

            CodeResponse fallback = new CodeResponse();
            fallback.setSummary("AI did not return proper JSON");
            fallback.setScore(0);

            List<String> errors = new ArrayList<>();
            errors.add(aiResponse);

            fallback.setBugs(errors);

            return fallback;
        }
    }

    @Value("${groq.api.key}")
    private String apiKey;

    private String callAI(String prompt) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.1-8b-instant");

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", prompt);

        messages.add(msg);
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map<?, ?> responseBody = response.getBody();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) responseBody.get("choices");

        Map<String, Object> choice = choices.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> message =
                (Map<String, Object>) choice.get("message");

        return message.get("content").toString();
    }
}