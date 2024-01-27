package ru.hoiboi.gfdocsbot.constant;

public enum CompanyEnum {
    START("ООО «СТАРТ»"),
    VEBLOGISTIC("ООО «ВЭБ ЛОГИСТИКА»");

    final String title;
    CompanyEnum(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
