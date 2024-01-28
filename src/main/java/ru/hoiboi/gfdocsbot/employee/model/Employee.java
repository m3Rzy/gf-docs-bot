package ru.hoiboi.gfdocsbot.employee.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hoiboi.gfdocsbot.employee.model.passport.Passport;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Employee {
    @NotNull
    private Passport passport;
    @NotNull
    private String inn;
    @NotNull
    private String numberOfPension;
    @NotNull
    private String rs;
    @NotNull
    private String bank;
    @NotNull
    private String telephone;
}
