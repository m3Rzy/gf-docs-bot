package ru.hoiboi.gfdocsbot.employee.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import ru.hoiboi.gfdocsbot.core.TelegramDocsBot;
import ru.hoiboi.gfdocsbot.employee.model.Employee;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.hoiboi.gfdocsbot.util.io.FileManager.*;
import static ru.hoiboi.gfdocsbot.util.io.FileManager.convertToShortName;

@Slf4j
public class EmployeeServiceIo {

    public static void startService(String fileName, Employee employee, long chatId,
                                    TelegramDocsBot telegramDocsBot, String companyTitle) {
        try {
            try (InputStream inputStream = new FileInputStream(".\\src/main/resources/pattern/employee/"
                    + fileName + ".docx")) {
                XWPFDocument document = openDocument(inputStream);
                changeDocument(document, employee, chatId, telegramDocsBot, companyTitle);
                log.info("Договор успешно создан и отправлен!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании и отправке документа", e);
        }
    }


    private static void changeDocument(XWPFDocument document, Employee employee, long chatId,
                                       TelegramDocsBot telegramDocsBot, String company) {
        try {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text.contains("fio")) {
                            text = text.replace("fio", employee.getPassport().getFio());
                            r.setText(text, 0);
                        }

                        if (text.contains("dateOfBirth")) {
                            text = text.replace("dateOfBirth", employee.getPassport()
                                    .getDateOfBirth().toString());
                            r.setText(text, 0);
                        }

                        if (text.contains("serialNumber")) {
                            text = text.replace("serialNumber", employee.getPassport().getSerialNumber());
                            r.setText(text, 0);
                        }

                        if (text.contains("register")) {
                            text = text.replace("register", employee.getPassport().getRegister());
                            r.setText(text, 0);
                        }

                        if (text.contains("dateOfIssue")) {
                            text = text.replace("dateOfIssue", employee.getPassport()
                                    .getDateOfIssue().toString());
                            r.setText(text, 0);
                        }

                        if (text.contains("code")) {
                            text = text.replace("code", employee.getPassport().getCode());
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
                                if (text != null && text.contains("fio")) {
                                    text = text.replace("fio", employee.getPassport().getFio());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text != null && text.contains("dateOfBirth")) {
                                    text = text.replace("dateOfBirth", employee.getPassport()
                                            .getDateOfBirth().toString());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text != null && text.contains("placeOfBirth")) {
                                    text = text.replace("placeOfBirth", employee.getPassport()
                                            .getPlaceOfBirth());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("serialNumber")) {
                                    text = text.replace("serialNumber", employee.getPassport()
                                            .getSerialNumber());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("dateOfIssue")) {
                                    text = text.replace("dateOfIssue", employee.getPassport()
                                            .getDateOfIssue().toString());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("register")) {
                                    text = text.replace("register", employee.getPassport().getRegister());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("code")) {
                                    text = text.replace("code", employee.getPassport().getCode());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("registration")) {
                                    text = text.replace("registration", employee.getPassport()
                                            .getRegistration());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("inn")) {
                                    text = text.replace("inn", employee.getInn());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("numberOfPension")) {
                                    text = text.replace("numberOfPension", employee.getNumberOfPension());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("rs")) {
                                    text = text.replace("rs", employee.getRs());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("bank")) {
                                    text = text.replace("bank", employee.getBank());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("telephone")) {
                                    text = text.replace("telephone", employee.getTelephone());
                                    r.setText(text, 0);
                                }

                                text = r.getText(0);
                                if (text.contains("short")) {
                                    text = text.replace("short", convertToShortName(employee
                                            .getPassport().getFio()));
                                    r.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }
            String fileName = company + "_" + convertToSurname(convertToShortName(employee
                    .getPassport().getFio()))
                    + "_" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy_hh.mm")) + ".docx";

            if (Files.isExecutable(Path.of(".\\src/main/resources/output/employee"))) {
                System.out.println("Каталог уже сущесвует!");
            } else {
                new File(".\\src/main/resources/output/employee").mkdirs();
                System.out.println("Папка document успешно создана!");
            }

            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(".\\src/main/resources/output/employee/" + fileName)) {
                document.write(fileOutputStream);
                document.close();

                File file = new File(".\\src/main/resources/output/employee/" + fileName);
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
}
