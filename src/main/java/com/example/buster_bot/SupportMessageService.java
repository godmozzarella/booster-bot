package com.example.buster_bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SupportMessageService {

    private Map<String, List<String>> messages;
    private final Random random = new Random();

    public SupportMessageService() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("supportMessages.json")) {
            messages = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            messages = Map.of("default", List.of("Ты замечательный человек!"));
        }
    }
    public String getRandomMessage(String category) {
        List<String> list = messages.get(category);
        if (list == null || list.isEmpty()) return "";
        return list.get(random.nextInt(list.size()));
    }
}