package org.home.model;

public class Frequency {
    private final String name;

    private Frequency(String name) {
        this.name = name;
    }

    public static final Frequency DAILY = new Frequency("DAILY");
    public static final Frequency WEEKLY = new Frequency("WEEKLY");

    public static Frequency fromString(String value) {
        switch (value.toUpperCase()) {
            case "DAILY":
                return DAILY;
            case "WEEKLY":
                return WEEKLY;
            default:
                throw new IllegalArgumentException("Invalid frequency: " + value);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
