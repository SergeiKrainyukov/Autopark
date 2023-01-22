package com.example.demo3.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;

    public Bot(BotConfig config) {
        this.config = config;
    }

    //TODO: set real answers
    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getMessage().getText();
        } else {
            chatId = update.getChannelPost().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getChannelPost().getText();
        }

        if (messageText.contains("/hello")) {
            builder.text("Привет");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }

        if (messageText.contains("/chartId")) {
            builder.text("ID Канала : " + chatId);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }

        if (messageText.contains("/vehicle_mileage")) {
            builder.text("write state number of vehicle");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("101")) {
            builder.text("write period");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("10.01.2023")) {
            builder.text("55km");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("11.01.2023-10.02.2023")) {
            builder.text("100km");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
    }


    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}
