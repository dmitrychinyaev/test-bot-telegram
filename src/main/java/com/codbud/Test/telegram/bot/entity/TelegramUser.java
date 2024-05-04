package com.codbud.Test.telegram.bot.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class TelegramUser {
    private Long chatId;
    private String username;
    private String firstname;
    private String lastname;
}
