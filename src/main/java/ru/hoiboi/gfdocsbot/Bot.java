package ru.hoiboi.gfdocsbot;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hoiboi.gfdocsbot.constant.Emojis;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final List<Long> users = new ArrayList<>();

    @Override
    public String getBotUsername() {
        return "GfDocsBot";
    }

    @Override
    public String getBotToken() {
        return "6537325876:AAFVi7gll6H7ZUy8pz3epXk_BdOcVu_5fNU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("{}: {}", update.getMessage().getFrom().getUserName(), update.getMessage().getText());
            if (isNewUser(update.getMessage().getFrom().getId())) {
                try {
                    showMenu(update.getMessage().getChatId(), update.getMessage().getFrom().getFirstName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals("info")) {
                try {
                    setAnswer(chatId, "Вы нажали на кнопку информации.");
                    log.info("{} нажал на кнопку info", update.getCallbackQuery().getFrom().getUserName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (call_data.equals("create_document")) {
                try {
                    log.info("{} нажал на кнопку create_document", update.getCallbackQuery()
                            .getFrom().getUserName());
                    selectMenuDocument(chatId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (call_data.equals("individual")) {
                try {
                    setAnswer(chatId, "Вы выбрали договор для ИП.");
                    log.info("{} нажал на кнопку individual", update.getCallbackQuery()
                            .getFrom().getUserName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (call_data.equals("self-employed")) {
                try {
                    setAnswer(chatId, "Вы выбрали договор для самозанятого.");
                    log.info("{} нажал на кнопку self-employed", update.getCallbackQuery()
                            .getFrom().getUserName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (call_data.equals("company")) {
                try {
                    setAnswer(chatId, "Вы выбрали договор для ООО.");
                    log.info("{} нажал на кнопку company", update.getCallbackQuery()
                            .getFrom().getUserName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void showMenu(Long chatId, String firstName) throws TelegramApiException {
        InlineKeyboardMarkup keyboard;

        var helpButton = InlineKeyboardButton.builder()
                .text("О приложении " + EmojiParser.parseToUnicode(Emojis.INFORMATION_SOURCE_EMOJI))
                .callbackData("info")
                .build();

        var startButton = InlineKeyboardButton.builder()
                .text("Создать документ " + EmojiParser.parseToUnicode(Emojis.PAGE_FACING_UP_EMOJI))
                .callbackData("create_document")
                .build();

        keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(helpButton))
                .keyboardRow(List.of(startButton))
                .build();

        SendMessage message = SendMessage.builder().chatId(chatId).parseMode(ParseMode.HTML)
                .text("Добро пожаловать, _" + firstName + "_ "
                        + EmojiParser.parseToUnicode(Emojis.WAVE_EMOJI) + "\n\n"
                        + "Ваш персональный мастер-документов. Создает договора по готовым шаблонам " +
                        "с уникальным стилем. Эффективно, точно, быстро. Разнообразие форматов. " +
                        "Переведем документы на новый уровень.\n\n"
                        + EmojiParser.parseToUnicode(Emojis.ZAP_EMOJI) + " Нашел ошибку или есть предложения " +
                        "по улучшению? @hoiboui")
                .replyMarkup(keyboard).build();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        execute(message);
    }

    private void selectMenuDocument(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard;

        var individualButton = InlineKeyboardButton.builder()
                .text("Договор для ИП " + EmojiParser.parseToUnicode(Emojis.BRIEFCASE_EMOJI))
                .callbackData("individual")
                .build();

        var selfEmployedButton = InlineKeyboardButton.builder()
                .text("Договор для самозанятого " + EmojiParser.parseToUnicode(Emojis.BUST_IN_SILHOUETTE_EMOJI))
                .callbackData("self-employed")
                .build();

        var companyButton = InlineKeyboardButton.builder()
                .text("Договор для ООО " + EmojiParser.parseToUnicode(Emojis.BUSTS_IN_SILHOUETTE_EMOJI))
                .callbackData("company")
                .build();

        keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(individualButton))
                .keyboardRow(List.of(selfEmployedButton))
                .keyboardRow(List.of(companyButton))
                .build();

        SendMessage message = SendMessage.builder().chatId(chatId).parseMode(ParseMode.HTML)
                .text("Выберите для _КОГО_ делаем договор: \n")
                .replyMarkup(keyboard).build();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        execute(message);
    }

    private void setAnswer(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    private boolean isNewUser(Long id) {
        if (users.contains(id)) {
            return false;
        } else {
            users.add(id);
            return true;
        }
    }
}
