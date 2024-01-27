package ru.hoiboi.gfdocsbot;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hoiboi.gfdocsbot.constant.CompanyEnum;
import ru.hoiboi.gfdocsbot.constant.Emojis;
import ru.hoiboi.gfdocsbot.individual.model.Individual;
import org.apache.commons.lang3.ObjectUtils;
import ru.hoiboi.gfdocsbot.individual.service.IndividualDockService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.hoiboi.gfdocsbot.constant.Emojis.*;

@Slf4j
@Component
public class TelegramDocsBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    private String company = "";
    private String titleOfCompany = "";
    private boolean isState = false;
    private final List<Long> users = new ArrayList<>();

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
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
                                    IndividualDockService.startService(company, individual,
                                            update.getMessage().getChatId(), this, titleOfCompany);
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

    public void sendDocument(long chatId, String fileName, InputStream inputStream) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(inputStream, ".\\src/resources/document/" + fileName));
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.getMessage();
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
                titleOfCompany = "СТАРТ_ИП";
                listenDataForIndividual(chatId);
                break;
            case "individual_DILIZH":
                company = "individual_DILIZH";
                titleOfCompany = "ДИЛИЖАНС_СТОЛИЦА_ИП";
                listenDataForIndividual(chatId);
                break;
            case "individual_RADIUS":
                company = "individual_RADIUS";
                titleOfCompany = "РАДИУС_ИП";
                listenDataForIndividual(chatId);
                break;
            case "individual_RUSTRANS":
                company = "individual_RUSTRANS";
                titleOfCompany = "РУСТРАНСПЕРЕВОЗКА_ИП";
                listenDataForIndividual(chatId);
                break;
            case "individual_VEBLOGISTIC":
                company = "individual_VEBLOGISTIC";
                titleOfCompany = "ВЭБЛОГИСТИКА_ИП";
                listenDataForIndividual(chatId);
                break;

        }
    }

    private void listenDataForIndividual(long chatId) {
        setAnswer(chatId, EmojiParser.parseToUnicode(EXCLAMATION_EMOJI) + """
                 *Ниже необходимо заполнить данные подписуемого строго по порядку:*
                
                - Наименование ИП
                - Юр. адрес
                - ИНН
                - ОГРНИП
                - БИК
                - Наименование банка
                - Расчетный счет
                - Корресподентский счет
                - Телефон для связи
                - Почта""");
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
        individual.setTelephone(lines[8].trim());
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

        InlineKeyboardMarkup keyboard;

//        var helpButton = createInlineKeyboardButton("О приложении " + EmojiParser
//                .parseToUnicode(INFORMATION_SOURCE_EMOJI), "info");
        var startButton = createInlineKeyboardButton("Создать документ " + EmojiParser
                .parseToUnicode(PAGE_FACING_UP_EMOJI), "create_document");

        keyboard = InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(helpButton))
                .keyboardRow(List.of(startButton))
                .build();

        SendMessage message = SendMessage.builder().chatId(chatId).parseMode(ParseMode.HTML)
                .text("Добро пожаловать, _" + firstName + "_ "
                        + EmojiParser.parseToUnicode(WAVE_EMOJI) + "\n\n"
                        + "Ваш персональный мастер-документов. Создает договоры по готовым шаблонам " +
                        "с уникальным стилем. Эффективно, точно, быстро. Разнообразие форматов. " +
                        "Переведем документы на новый уровень.\n\n"
                        + EmojiParser.parseToUnicode(ZAP_EMOJI) + " Нашел ошибку или есть предложения " +
                        "по улучшению? @hoiboui")
                .replyMarkup(keyboard).build();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        execute(message);
    }

    private void selectCompanyForIndividual(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (CompanyEnum e : CompanyEnum.values()) {
            rowsInline.add(List.of(createInlineKeyboardButton(e.toString(), e.getComment())));
        }

        markupInline.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder().chatId(chatId).parseMode(ParseMode.HTML)
                .text("Выберите кампанию, с которой оформляем договор: \n")
                .replyMarkup(markupInline).build();
        message.enableHtml(true);
        message.enableMarkdownV2(true);
        message.enableMarkdown(true);
        message.setReplyMarkup(markupInline);
        execute(message);
    }

    private void selectMenuDocument(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard;

        var individualButton = createInlineKeyboardButton("Договор для ИП " + EmojiParser
                .parseToUnicode(Emojis.BRIEFCASE_EMOJI), "individual");

        var selfEmployedButton = createInlineKeyboardButton("Договор для самозанятого (не работает) " + EmojiParser
                .parseToUnicode(Emojis.BUST_IN_SILHOUETTE_EMOJI), "self-employed");

        var companyButton = createInlineKeyboardButton("Договор для ООО (не работает) " + EmojiParser
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
