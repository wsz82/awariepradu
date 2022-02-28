package io.wsz82.awariepradu.database;

public enum SingleValueNumber {
    LAST_CHECKED_LENGTH (1);

    private final int id;

    SingleValueNumber(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
