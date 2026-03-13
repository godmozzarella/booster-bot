package com.example.buster_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramSendService {

    private final OkHttpTelegramClient telegramClient;
    private final TestService testService;
    private final SupportMessageService supportMessageService;

    public TelegramSendService(@Value("${telegram.bot.token}") String token, TestService testService, SupportMessageService supportMessageService) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.testService = testService;
        this.supportMessageService = supportMessageService;
    }

    public void sendText(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTextWithRemovedKeyboard(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTestQuestion(Long chatId, Long userId) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < 2; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int i = 1 + rowIndex * 5; i <= (rowIndex + 1) * 5; i++) {
                row.add(String.valueOf(i));
            }
            keyboardRows.add(row);
        }
        String questionText = testService.getNextQuestion(userId);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(questionText)
                .replyMarkup(keyboardMarkup)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCompliment(Long chatId, int score, String firstName) {
        String category;
        if (score <= 10) category = "strong_sad";
        else if (score <= 20) category = "sad";
        else if (score <= 30) category = "neutral";
        else if (score <= 40) category = "good";
        else category = "excellent";

        String message = supportMessageService.getRandomMessage(category);

        sendTextWithRemovedKeyboard(chatId, firstName + ", " + message);
    }
}
