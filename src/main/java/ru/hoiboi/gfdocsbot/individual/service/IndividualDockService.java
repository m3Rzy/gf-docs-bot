package ru.hoiboi.gfdocsbot.individual.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import ru.hoiboi.gfdocsbot.individual.model.Individual;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class IndividualDockService {

    public static void startService(String fileName, Individual individual) {
        try {
            XWPFDocument document = openDocument(".\\src/main/resources/pattern/" + fileName + ".docx");
            log.info("Договор {} успешно открыт.", fileName + ".docx");
            changeDocument(document, individual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void changeDocument(XWPFDocument document, Individual individual) throws IOException {
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
                                text = text.replace("telephone", individual.getTelephone().toString());
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
        document.write(new FileOutputStream(".\\src/main/resources/document/" + individual.getInn()
                + "_" + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".docx"));
        document.close();
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

    private static XWPFDocument openDocument(String filename) throws Exception {
        FileInputStream inputStream = new FileInputStream(filename);
        XWPFDocument document = new XWPFDocument(inputStream);
        inputStream.close();
        return document;
    }
}
