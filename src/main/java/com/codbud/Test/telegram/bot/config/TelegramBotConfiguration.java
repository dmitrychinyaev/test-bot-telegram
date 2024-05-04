package com.codbud.Test.telegram.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramBotConfiguration {
    @Value("${bot.name}")
    private String botname;
    @Value("${bot.token}")
    private String token;
}
