package ru.hoiboi.gfdocsbot.employee.model.passport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Passport {
    private String fio;
    private String serial;
    private String number;
    private String code;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String register;
    private LocalDate dateOfIssue;
}
