package com.example.buster_bot;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import java.util.Map;
import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TestService testService;
    private final TelegramSendService sendService;
    private final CommandHandlerService commandHandlerService;
    private final Map<String, Consumer<Update>> commandHandlers;

    public UpdateConsumer(
            TestService testService,
            TelegramSendService sendService,
            CommandHandlerService commandHandlerService
    ) {
        this.testService = testService;
        this.sendService = sendService;
        this.commandHandlerService = commandHandlerService;

        this.commandHandlers = Map.of(
                "/start", commandHandlerService::handleStart,
                "/compliment", commandHandlerService::handleCompliment,
                "/test", commandHandlerService::handleTest
        );
    }

    @Override
    public void consume(Update update) {
        if (update.getMessage() == null || !update.getMessage().hasText()) return;

        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        if (testService.getNextQuestion(userId) != null) {
            testService.handleAnswer(update, userId, chatId, message, sendService);
            return;
        }

        if (message.startsWith("/")) {
            commandHandlers
                    .getOrDefault(message, commandHandlerService::handleUnknownCommand)
                    .accept(update);
            return;
        }

        sendService.sendText(chatId, "Пожалуйста, используй команды или кнопки для взаимодействия с ботом:\n" +
                "/start — начать бота\n" +
                "/compliment — получить комплимент\n" +
                "/test — пройти тест и получить поддержку");
    }
}