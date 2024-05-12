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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

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

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test.csv", true))) {
                bufferedWriter.write(String.valueOf(telegramUser.getChatId()));
                bufferedWriter.write(',');
            }



            switch (message) {
                case "/start" -> sendMessage(chatId, "Hello, " + username + " введите операцию");
                case "/help" -> sendMessage(chatId,  "Try to send something");
                case "/info" -> sendMessage(chatId, "Information about bot");
                case "/users" -> sendMessage(chatId, telegramUserSet.toString());
                case "/sendToUsers" -> sendMessagesToUsers();
                case "/showUsers" -> showUsers(chatId);
                case "/showPic" -> sendPhoto(chatId, "123.jpg");
                case "/showBoard" -> getKeyboard(String.valueOf(chatId), "Это тестовая клавиатура");
            }

            if(isEmail(message)){
                sendMessage(chatId, "Это какой e-mail");
            }
        }

        if(update.hasCallbackQuery()){
            String callback = update.getCallbackQuery().getData();
            if(callback.equals("test1")) {
                String listOfUsers;
                try (Scanner scanner = new Scanner(new File("test.csv"))) {
                    listOfUsers = scanner.nextLine();
                }
                sendMessage(update.getCallbackQuery().getMessage().getChatId(), listOfUsers);
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                sendMessage.setText(callback);
                execute(sendMessage);
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

    private void getKeyboard(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        //Текст над клавиатурой
        message.setText(text);
        //Объявляем клавиатуру
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        //Объявляем хранилище строк
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        //Заводим две строки
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        //Делаем кнопки
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("test1");
        button.setCallbackData("test1");
        rowInLine1.add(button);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("test2");
        button2.setCallbackData("Это ответ от кнопки test2");
        rowInLine1.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("test3");
        button3.setCallbackData("Это ответ от кнопки test3");
        rowInLine2.add(button3);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("test4");
        button4.setCallbackData("Это ответ от кнопки test4");
        rowInLine2.add(button4);
        //Добавляем строки в хранилище
        rowsInLine.add(rowInLine1);
        rowsInLine.add((rowInLine2));
        //Добавляем хранилище в клавиатуру
        markupInLine.setKeyboard(rowsInLine);
        //Добавляем клавиатуру в message
        message.setReplyMarkup(markupInLine);

        execute(message);
    }
}
