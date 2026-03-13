package com.example.buster_bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

@Service
public class ComplimentService {
    private List<String> compliments;
    private final Random random = new Random();

    public ComplimentService() {
        loadCompliments();
    }

    private void loadCompliments() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("compliments.json")) {
            compliments = mapper.readValue(is, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            compliments = List.of("Ты замечательный человек!");
        }
    }

    public String getRandomCompliment() {
        return compliments.get(random.nextInt(compliments.size()));
    }
}