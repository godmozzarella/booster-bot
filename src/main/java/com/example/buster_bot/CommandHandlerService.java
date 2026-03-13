package com.example.buster_bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class CommandHandlerService {

    private final ComplimentService complimentService;
    private final TestService testService;
    private final TelegramSendService sendService;

    public CommandHandlerService(
            ComplimentService complimentService,
            SupportMessageService supportMessageService,
            TestService testService,
            TelegramSendService sendService
    ) {
        this.complimentService = complimentService;
        this.testService = testService;
        this.sendService = sendService;
    }

    public void handleStart(Update update) {
        String firstName = update.getMessage().getFrom().getFirstName();
        Long chatId = update.getMessage().getChatId();

        String text = "Привет, " + firstName + "! 👋\n\n" +
                "Я бот поддержки и хорошего настроения ✨\n" +
                "Моя задача — немного поднять тебе настроение.\n\n" +
                "Получить комплимент прямо сейчас:\n" +
                "/compliment — получить комплимент\n";

        sendService.sendText(chatId, text);
    }

    public void handleCompliment(Update update) {
        String firstName = update.getMessage().getFrom().getFirstName();
        Long chatId = update.getMessage().getChatId();

        String compliment = complimentService.getRandomCompliment();
        sendService.sendText(chatId, firstName + ", " + compliment);
    }

    public void handleTest(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        testService.startTest(userId);
        sendService.sendTestQuestion(chatId, userId);
    }

    public void handleUnknownCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        sendService.sendText(chatId, "Неизвестная команда. Пожалуйста, используйте одну из доступных команд.");
    }
}