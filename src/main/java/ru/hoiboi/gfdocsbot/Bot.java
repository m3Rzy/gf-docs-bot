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
import ru.hoiboi.gfdocsbot.constant.CompanyEnum;
import ru.hoiboi.gfdocsbot.constant.Emojis;
import ru.hoiboi.gfdocsbot.individual.model.Individual;
import ru.hoiboi.gfdocsbot.individual.service.IndividualDockService;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private String company = "";
    private boolean isState = false;
    private final List<Long> users = new ArrayList<>();
    private final List<Individual> individuals = new ArrayList<>();

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
                    mainMenu(update.getMessage().getChatId(), update.getMessage().getFrom().getFirstName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                switch (update.getMessage().getText()) {
                    case "/start" -> {
                        try {
                            mainMenu(update.getMessage().getChatId(),
                                    update.getMessage().getFrom().getFirstName());
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    default -> {
                        if (isState) {
                            try {
                                Individual individual;
                                individual = createIndividualFromString(update.getMessage().getText());
                                if (areAllFieldsFilled(individual)) {
                                    individuals.add(individual);
                                    IndividualDockService.startService(company, individual);
                                    setAnswer(update.getMessage().getChatId(), "Файл успешно сохранён!");
                                    isState = false;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                setAnswer(update.getMessage().getChatId(), "Не все поля заполнены!");
                            }
                        } else {
                            setAnswer(update.getMessage().getChatId(), "Неизвестная команда! Начните со /start");
                        }
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            try {
                handleCallbackQuery(call_data, chatId, update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleCallbackQuery(String callData, long chatId, Update update) throws TelegramApiException {
        switch (callData) {
            case "info":
                log.info("{} нажал на кнопку info", update.getCallbackQuery().getFrom().getUserName());
                break;
            case "create_document":
                log.info("{} нажал на кнопку create_document", update.getCallbackQuery().getFrom().getUserName());
                selectMenuDocument(chatId);
                break;
            case "individual":
                selectCompanyForIndividual(chatId);
                log.info("{} нажал на кнопку individual", update.getCallbackQuery().getFrom().getUserName());
                break;
            case "self-employed":
                log.info("{} нажал на кнопку self-employed", update.getCallbackQuery().getFrom().getUserName());
                break;
            case "company":
                log.info("{} нажал на кнопку company", update.getCallbackQuery().getFrom().getUserName());
                break;
            case "individual_START":
                company = "individual_START";
                listenDataForIndividual(chatId);
                break;
        }
    }

    private void listenDataForIndividual(long chatId) throws TelegramApiException {
        setAnswer(chatId, "*Ниже необходимо заполнить данные подписуемого строго по порядку:*" +
                "\nНаименование ИП" +
                "\nЮр. адрес" +
                "\nИНН" +
                "\nОГРНИП" +
                "\nБИК" +
                "\nНаименование банка" +
                "\nРасчетный счет" +
                "\nКорресподентский счет" +
                "\nТелефон для связи" +
                "\nПочта");
        isState = true;
    }

    private Individual createIndividualFromString(String input) {
        String[] lines = input.split("\\n");
        Individual individual = new Individual();

        individual.setTitle(lines[0].trim());
        individual.setAddress(lines[1].trim());
        individual.setInn(lines[2].trim());
        individual.setRegisterNumber(lines[3].trim());
        individual.setBic(lines[4].trim());
        individual.setBank(lines[5].trim());
        individual.setRs(lines[6].trim());
        individual.setKs(lines[7].trim());
        individual.setTelephone(Long.valueOf(lines[8].trim()));
        individual.setMail(lines[9].trim());
        log.info("Подписуемый {} успешно сформирован!", individual);
        return individual;
    }

    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private void mainMenu(Long chatId, String firstName) throws TelegramApiException {

//        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
//
//        replyKeyboard.setResizeKeyboard(true);
//        replyKeyboard.setOneTimeKeyboard(true);
//        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRows.add(keyboardRow);
//        keyboardRow.add(new KeyboardButton("О приложении " + EmojiParser
//                .parseToUnicode(Emojis.INFORMATION_SOURCE_EMOJI)));
//        keyboardRow.add(new KeyboardButton("Создать документ " + EmojiParser
//                .parseToUnicode(Emojis.PAGE_FACING_UP_EMOJI)));
//        replyKeyboard.setKeyboard(keyboardRows);

        InlineKeyboardMarkup keyboard;

        var helpButton = createInlineKeyboardButton("О приложении " + EmojiParser
                .parseToUnicode(Emojis.INFORMATION_SOURCE_EMOJI), "info");
        var startButton = createInlineKeyboardButton("Создать документ " + EmojiParser
                .parseToUnicode(Emojis.PAGE_FACING_UP_EMOJI), "create_document");

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

    private void selectCompanyForIndividual(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard;
        var companyStartButton = createInlineKeyboardButton(CompanyEnum.START.toString(), "individual_START");

        keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(companyStartButton))
                .build();

        SendMessage message = SendMessage.builder().chatId(chatId).parseMode(ParseMode.HTML)
                .text("Выберите кампанию, с которой оформляем договор: \n")
                .replyMarkup(keyboard).build();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        execute(message);
    }

    private void selectMenuDocument(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard;

        var individualButton = createInlineKeyboardButton("Договор для ИП " + EmojiParser
                .parseToUnicode(Emojis.BRIEFCASE_EMOJI), "individual");

        var selfEmployedButton = createInlineKeyboardButton("Договор для самозанятого " + EmojiParser
                .parseToUnicode(Emojis.BUST_IN_SILHOUETTE_EMOJI), "self-employed");

        var companyButton = createInlineKeyboardButton("Договор для ООО " + EmojiParser
                .parseToUnicode(Emojis.BUSTS_IN_SILHOUETTE_EMOJI), "company");

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

    private boolean areAllFieldsFilled(Object obj) {
        return ObjectUtils.allNotNull(obj);
    }

    private void setAnswer(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
