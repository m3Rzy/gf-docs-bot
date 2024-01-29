package ru.hoiboi.gfdocsbot.util.constant;

import lombok.Getter;

@Getter
public enum CompanyEnum {
    START("ООО «СТАРТ»", "_START"),
    DILIZH("ООО «ДИЛИЖАНС СТОЛИЦА»", "_DILIZH"),
    RADIUS("ООО «РАДИУС»", "_RADIUS"),
    RUSTRANS("ООО «РУСТРАНСПЕРЕВОЗКА»", "_RUSTRANS"),
    GERAKLION("ООО «ГЕРАКЛИОН»", "_GERAKLION"),
    VEBLOGISTIC("ООО «ВЭБ ЛОГИСТИКА»", "_VEBLOGISTIC");

    final String title;
    final String comment;

    CompanyEnum(String title, String comment) {
        this.title = title;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return title;
    }
}
