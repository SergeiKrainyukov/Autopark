package com.example.demo3.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {

    // Имя бота заданное при регистрации
    @Value("${bot.name}")
    String botUserName;

    // Токен полученный при регистрации
    @Value("${bot.token}")
    String token;
}
