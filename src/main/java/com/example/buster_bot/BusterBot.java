package com.example.buster_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;


@Component
public class BusterBot implements SpringLongPollingBot {

    private  final UpdateConsumer updateConsumer;
    @Value("${telegram.bot.token}")
    private String botToken;

    public BusterBot(UpdateConsumer updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
