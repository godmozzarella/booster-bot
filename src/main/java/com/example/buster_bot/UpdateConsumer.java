package com.example.buster_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final ComplimentService complimentService;
    private final Map<String, Consumer<Update>> commandHandlers;

    public UpdateConsumer(@Value("${telegram.bot.token}") String botToken, ComplimentService complimentService) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.complimentService = complimentService;
        this.commandHandlers = Map.of(
            "/start", this::handleStart,
            "/compliment", this::handleCompliment
        );
        setupCommands();
    }

    public void setupCommands() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Запустить бота"),
                new BotCommand("/compliment", "Получить комплимент")
        );

        try {
            telegramClient.execute(new SetMyCommands(commands));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(Update update) {

        if (update.getMessage() == null || !update.getMessage().hasText()) {
            return;
        }

        String message = update.getMessage().getText();

        String username = update.getMessage()
                .getFrom()
                .getUserName();

        String firstName = update.getMessage()
                .getFrom()
                .getFirstName();

        Long userId = update.getMessage()
                .getFrom()
                .getId();

        //Лог
        System.out.printf(
                "\nПришло сообщение %s от %s с тегом @%s (id: %s)",
                message,
                firstName,
                username,
                userId
        );


        //Команды
        if (message.startsWith("/")) {
            commandHandlers.getOrDefault(message, this::handleUnknownCommand).accept(update);
        }
    }

    private void handleStart(Update update) {
        System.out.println("Пользователь запустил бота");
    }

    private void handleCompliment(Update update) {
        String firstName = update.getMessage()
                .getFrom()
                .getFirstName();
        var chatId = update.getMessage().getChatId();

        String compliment = complimentService.getRandomCompliment();
        SendMessage sendMessage = SendMessage.builder()
                .text("Привет, " + firstName + "! " + compliment
                )
                .chatId(chatId)
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleUnknownCommand(Update update) {
        System.out.println("Неизвестная команда");
    }

}