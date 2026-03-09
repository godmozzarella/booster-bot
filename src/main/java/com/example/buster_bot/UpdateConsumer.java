package com.example.buster_bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

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

            System.out.printf(
                    "\nПришло сообщение %s от %s с тегом @%s (id: %s)",
                    message,
                    firstName,
                    username,
                    userId
            );

            if (message.startsWith("/")) {

                switch (message) {

                    case "/start":
                        System.out.println("Пользователь запустил бота");
                        break;

                    case "/compliment":
                        System.out.println("Пользователь запросил комплимент");
                        break;

                    default:
                        System.out.println("Неизвестная команда");
                }
            }
        }

}

