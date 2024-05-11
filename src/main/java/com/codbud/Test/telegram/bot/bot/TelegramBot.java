package com.codbud.Test.telegram.bot.bot;

import com.codbud.Test.telegram.bot.config.TelegramBotConfiguration;
import com.codbud.Test.telegram.bot.entity.TelegramUser;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.codbud.Test.telegram.bot.utils.RegexUtils.isEmail;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfiguration telegramBotConfiguration;

    private final Set<TelegramUser> telegramUserSet;

    public TelegramBot(TelegramBotConfiguration telegramBotConfiguration, Set<TelegramUser> telegramUserSet) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.telegramUserSet = new HashSet<>();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            String username = update.getMessage().getChat().getFirstName();

            TelegramUser telegramUser = TelegramUser.builder()
                    .chatId(update.getMessage().getChatId())
                    .username(update.getMessage().getChat().getUserName())
                    .firstname(update.getMessage().getChat().getFirstName())
                    .lastname(update.getMessage().getChat().getLastName())
                    .build();

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("notes.txt", true))) {
                bufferedWriter.write(String.valueOf(telegramUser));
                bufferedWriter.write('\n');
            }

            switch (message) {
                case "/start" -> sendMessage(chatId, "Hello, " + username + " введите операцию");
                case "/help" -> sendMessage(chatId,  "Try to send something");
                case "/info" -> sendMessage(chatId, "Information about bot");
                case "/users" -> sendMessage(chatId, telegramUserSet.toString());
                case "/sendToUsers" -> sendMessagesToUsers();
                case "/showUsers" -> showUsers(chatId);
                case "/showPic" -> sendPhoto(chatId, "123.jpg");
            }

            if(isEmail(message)){
                sendMessage(chatId, "Это какой e-mail");
            }
        }

    }



    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotname();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    private void sendMessage(long chatId, String textToSend) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);

        execute(sendMessage);
    }

    private void sendMessagesToUsers() throws TelegramApiException {

        Iterator<TelegramUser> telegramUserIterator = telegramUserSet.iterator();

        if(telegramUserIterator.hasNext()){
            TelegramUser telegramUser = telegramUserIterator.next();
            sendMessage(telegramUser.getChatId(), "Это реклама");
        }

    }

    private void showUsers(long chatID) throws TelegramApiException {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fin = new FileInputStream("notes.txt");
             BufferedInputStream bis = new BufferedInputStream(fin)) {

            int i;
            while ((i = fin.read()) != -1) {
                stringBuilder.append((char)i);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatID, stringBuilder.toString());
    }

    private void sendPhoto(long chatId, String filePath) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new File(filePath)));
        execute(sendPhoto);
    }
}
