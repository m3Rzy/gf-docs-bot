package ru.hoiboi.gfdocsbot.individual.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Individual {
    @NotNull
    private String title;
    @NotNull
    private String inn;
    @NotNull
    private String address;
    @NotNull
    private String registerNumber;
    @NotNull
    private String bic;
    @NotNull
    private String bank;
    @NotNull
    private String rs;
    @NotNull
    private String ks;
    @NotNull
    private String telephone;
    @NotNull
    private String mail;
}
