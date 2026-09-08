package com.lost2found.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Categories of lost and found items.
 */
public enum ItemCategory {
    ELECTRONICS,
    WALLETS_CARDS,
    JEWELRY,
    KEYS,
    DOCUMENTS,
    CLOTHING,
    BAGS_LUGGAGE,
    PETS,
    OTHER;

    @JsonCreator
    public static ItemCategory fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER;
        }
        String clean = value.trim().toUpperCase().replaceAll("[^A-Z0-9_]", "_");
        for (ItemCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(clean) || cat.name().equalsIgnoreCase(value.trim())) {
                return cat;
            }
        }
        if (clean.contains("BAG") || clean.contains("BACKPACK") || clean.contains("LUGGAGE")) {
            return BAGS_LUGGAGE;
        }
        if (clean.contains("DOC") || clean.contains("CARD") || clean.contains("ID")) {
            return DOCUMENTS;
        }
        if (clean.contains("WALLET")) {
            return WALLETS_CARDS;
        }
        if (clean.contains("ELEC") || clean.contains("PHONE") || clean.contains("LAPTOP")) {
            return ELECTRONICS;
        }
        if (clean.contains("JEWEL")) {
            return JEWELRY;
        }
        if (clean.contains("KEY")) {
            return KEYS;
        }
        if (clean.contains("CLOTH")) {
            return CLOTHING;
        }
        if (clean.contains("PET")) {
            return PETS;
        }
        return OTHER;
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

