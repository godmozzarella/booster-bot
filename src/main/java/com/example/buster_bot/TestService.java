package com.example.buster_bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    private final List<String> questions = List.of(
            "Вопрос 1/5: как ты себя чувствуешь сегодня?",
            "Вопрос 2/5: насколько ты доволен сегодняшним днём?",
            "Вопрос 3/5: насколько у тебя есть энергия что-то делать?",
            "Вопрос 4/5: насколько спокойно ты себя чувствуешь?",
            "Вопрос 5/5: как оцениваешь своё настроение на данный момент?"
    );

    private final Map<Long, TestSession> sessions = new HashMap<>();

    public TestSession startTest(Long userId) {
        TestSession session = new TestSession();
        sessions.put(userId, session);
        return session;
    }

    public String getNextQuestion(Long userId) {
        TestSession session = sessions.get(userId);
        if (session == null) return null;
        int index = session.getQuestionIndex();
        if (index >= questions.size()) return null;
        return questions.get(index);
    }

    public void submitAnswer(Long userId, int answer) {
        TestSession session = sessions.get(userId);
        if (session == null) return;
        session.addScore(answer);
        session.nextQuestion();
    }

    public TestSession finishTest(Long userId) {
        TestSession session = sessions.remove(userId);
        return session;
    }

    public void handleAnswer(Update update, Long userId, Long chatId, String message, TelegramSendService sendService) {
        int answerScore;
        try {
            answerScore = Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            sendService.sendText(chatId, "Пожалуйста, введите число от 1 до 10, используя кнопки.");
            sendService.sendTestQuestion(chatId, userId);
            return;
        }

        if (answerScore < 1 || answerScore > 10) {
            sendService.sendText(chatId, "Пожалуйста, выберите число от 1 до 10, используя кнопки.");
            sendService.sendTestQuestion(chatId, userId);
            return;
        }

        this.submitAnswer(userId, answerScore);

        String nextQuestion = this.getNextQuestion(userId);
        if (nextQuestion != null) {
            sendService.sendTestQuestion(chatId, userId);
        } else {
            TestSession session = this.finishTest(userId);
            int totalScore = session.getScore();
            sendService.sendCompliment(chatId, totalScore, update.getMessage().getFrom().getFirstName());
        }
    }
}
