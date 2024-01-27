package ru.hoiboi.gfdocsbot.constant;

import lombok.Getter;

@Getter
public enum CompanyEnum {
    START("ООО «СТАРТ»", "individual_START"),
    DILIZH("ООО «ДИЛИЖАНС СТОЛИЦА»", "individual_DILIZH"),
    RADIUS("ООО «РАДИУС»", "individual_RADIUS"),
    RUSTRANS("ООО «РУСТРАНСПЕРЕВОЗКА»", "individual_RUSTRANS"),
    GERAKLION("ООО «ГЕРАКЛИОН» (не работает)", "individual_GERAKLION"),
    VEBLOGISTIC("ООО «ВЭБ ЛОГИСТИКА»", "individual_VEBLOGISTIC");

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
