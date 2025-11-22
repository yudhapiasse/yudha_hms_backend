package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum Religion {
    ISLAM("Islam", "Islam"),
    PROTESTANT("Protestant Christian", "Kristen Protestan"),
    CATHOLIC("Catholic Christian", "Kristen Katolik"),
    HINDU("Hindu", "Hindu"),
    BUDDHIST("Buddhist", "Buddha"),
    CONFUCIAN("Confucian", "Konghucu"),
    OTHER("Other", "Lainnya");

    private final String englishName;
    private final String indonesianName;

    Religion(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
