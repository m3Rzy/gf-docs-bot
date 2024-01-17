package ru.hoiboi.gfdocsbot.individual.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import ru.hoiboi.gfdocsbot.TelegramDocsBot;
import ru.hoiboi.gfdocsbot.individual.model.Individual;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class IndividualDockService {

    public static InputStream inputStream;

    public static void startService(String fileName, Individual individual, long chatId,
                                    TelegramDocsBot telegramDocsBot, String companyTitle) {
        try {
            try (InputStream inputStream = new FileInputStream(".\\src/main/resources/pattern/" + fileName + ".docx")) {
                XWPFDocument document = openDocument(inputStream);
                changeDocument(document, individual, chatId, telegramDocsBot, companyTitle);
                log.info("Договор успешно создан и отправлен!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании и отправке документа", e);
        }
    }

    private static void changeDocument(XWPFDocument document, Individual individual, long chatId,
                                       TelegramDocsBot telegramDocsBot, String company) {
        try {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text.contains("entrep")) {
                            text = text.replace("entrep", individual.getTitle());
                            r.setText(text, 0);
                        }

                        if (text.contains("ogrnip")) {
                            text = text.replace("ogrnip", individual.getRegisterNumber());
                            r.setText(text, 0);
                        }
                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun r : p.getRuns()) {
                                String text = r.getText(0);
                                if (text != null && text.contains("day")) {
                                    text = text.replace("day", LocalDate.now()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                                    r.setText(text,0);
                                }

                                text = r.getText(0);
                                if (text != null && text.contains("entrep")) {
                                    text = text.replace("entrep", individual.getTitle());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text != null && text.contains("address")) {
                                    text = text.replace("address", individual.getAddress());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text != null && text.contains("inn")) {
                                    text = text.replace("inn", individual.getInn());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("ogrnip")) {
                                    text = text.replace("ogrnip", individual.getRegisterNumber());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("bic")) {
                                    text = text.replace("bic", individual.getBic());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("rs")) {
                                    text = text.replace("rs", individual.getRs());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("bank")) {
                                    text = text.replace("bank", individual.getBank());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("ks")) {
                                    text = text.replace("ks", individual.getKs());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("telephone")) {
                                    text = text.replace("telephone", individual.getTelephone());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("mmmail")) {
                                    text = text.replace("mmmail", individual.getMail());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("short")) {
                                    text = text.replace("short", convertToShortName(individual.getTitle()));
                                    r.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }
            String fileName = company + "_" + individual.getInn()
                    + "_" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy_hh.mm")) + ".docx";


            try (FileOutputStream fileOutputStream = new FileOutputStream(".\\src/main/resources/document/" + fileName)) {
                document.write(fileOutputStream);
                document.close();

                File file = new File(".\\src/main/resources/document/" + fileName);
                if (!file.exists() || file.length() == 0) {
                    throw new RuntimeException("Ошибка: файл документа пуст или не существует");
                }

                log.info("Размер файла документа: {} байт", file.length());

                telegramDocsBot.sendDocument(chatId, fileName, new FileInputStream(file));
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertToShortName(String fullName) {
        StringBuilder shortNameBuilder = new StringBuilder();
        String[] nameParts = fullName.split(" ");

        shortNameBuilder.append(nameParts[0]).append(" ");

        if (nameParts.length > 1) {
            shortNameBuilder.append(nameParts[1].charAt(0)).append(". ");
        }

        if (nameParts.length > 2) {
            shortNameBuilder.append(nameParts[2].charAt(0)).append(". ");
        }

        return shortNameBuilder.toString().trim();
    }

    private static XWPFDocument openDocument(InputStream inputStream) throws Exception {
        IndividualDockService.inputStream = inputStream;
        return new XWPFDocument(inputStream);
    }
}
