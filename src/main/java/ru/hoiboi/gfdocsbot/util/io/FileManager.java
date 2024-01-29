package ru.hoiboi.gfdocsbot.util.io;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;

public class FileManager {

    public static String convertToShortName(String fullName) {
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

    public static String convertToSurname(String fullName) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] nameParts = fullName.split(" ");
        stringBuilder.append(nameParts[0]).append(" ");
        return stringBuilder.toString().trim();
    }

    public static XWPFDocument openDocument(InputStream inputStream) throws Exception {
        return new XWPFDocument(inputStream);
    }
}
