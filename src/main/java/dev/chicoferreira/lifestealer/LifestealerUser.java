package dev.chicoferreira.lifestealer;

import java.util.UUID;

public class LifestealerUser {

    private final UUID uuid;
    private int hearts;

    public LifestealerUser(UUID uuid, int hearts) {
        this.uuid = uuid;
        this.hearts = hearts;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getHearts() {
        return hearts;
    }

    void setHearts(int hearts) {
        this.hearts = hearts;
    }
}
