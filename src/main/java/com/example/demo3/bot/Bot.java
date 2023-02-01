package com.example.demo3.bot;

import com.example.demo3.model.entity.ManagerEntity;
import com.example.demo3.repository.ManagersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;

    private ManagerEntity manager = new ManagerEntity();
    private boolean isAuthorized = false;

    private final ManagersRepository managersRepository;

    public Bot(BotConfig config, ManagersRepository managersRepository) {
        this.config = config;
        this.managersRepository = managersRepository;
    }

    //TODO: set real answers
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null || !message.hasText()) return;
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;
        try {
            if (update.getMessage() != null) {
                chatId = update.getMessage().getChatId().toString();
                builder.chatId(chatId);
                messageText = update.getMessage().getText();
            } else {
                chatId = update.getChannelPost().getChatId().toString();
                builder.chatId(chatId);
                messageText = update.getChannelPost().getText();
            }

            if (isAuthorized) {
                switch (messageText) {
                    case "/login": {
                        builder.text("You have been authorized earlier as " + manager.getUsername());
                        execute(builder.build());
                    }
                }
            } else {
                switch (messageText) {
                    case "/login": {
                        if (manager.getUsername().isBlank()) {
                            builder.text("Write login:");
                            execute(builder.build());
                            return;
                        } else if (manager.getPassword().isBlank()) {
                            builder.text("Write password:");
                            execute(builder.build());
                            return;
                        }
                    }
                    default: {
                        if (manager.getUsername().isBlank()) {
                            manager = new ManagerEntity();
                            manager.setUsername(messageText);
                            builder.text("Write password:");
                            execute(builder.build());
                        } else if (manager.getPassword().isBlank()) {
                            manager.setPassword(messageText);
                            if (validateCredentials()) {
                                isAuthorized = true;
                                builder.text("You are successfully authorized");
                                execute(builder.build());
                            } else {
                                isAuthorized = false;
                                manager = new ManagerEntity();
                                builder.text("Authorization failed. Write login:");
                                execute(builder.build());
                            }
                        }
                    }
                }
            }

//            if (messageText.contains("/hello")) {
//                builder.text("Привет");
//                execute(builder.build());
//            }
//
//            if (messageText.contains("/vehicle_mileage")) {
//                builder.text("write state number of vehicle");
//                execute(builder.build());
//            }
//            if (messageText.contains("101")) {
//                builder.text("write period");
//                execute(builder.build());
//            }
//            if (messageText.contains("10.01.2023")) {
//                builder.text("55km");
//                execute(builder.build());
//            }
//            if (messageText.contains("11.01.2023-10.02.2023")) {
//                builder.text("100km");
//                execute(builder.build());
//            }
        } catch (Exception e) {
            log.debug(e.toString());
        }
    }

    private boolean validateCredentials() {
        Iterable<ManagerEntity> managerEntities = managersRepository.findAll();
        for (ManagerEntity managerEntity : managerEntities) {
            if (manager.getUsername().equals(managerEntity.getUsername()) && manager.getPassword().equals(managerEntity.getPassword())) {
                manager = managerEntity;
                return true;
            }
        }
        return false;
    }


    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}
