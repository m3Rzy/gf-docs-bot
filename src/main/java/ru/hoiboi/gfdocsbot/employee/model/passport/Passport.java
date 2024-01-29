package ru.hoiboi.gfdocsbot.employee.model.passport;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Passport {
    @NotNull
    private String fio;
    @NotNull
    private String serialNumber;
    @NotNull
    private String code;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private String placeOfBirth;
    @NotNull
    private String register;
    @NotNull
    private LocalDate dateOfIssue;
    @NotNull
    private String registration;
}
