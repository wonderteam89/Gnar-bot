package xyz.gnarbot.gnar.db;

import xyz.gnarbot.gnar.Bot;

import java.beans.ConstructorProperties;

public class Key implements ManagedObject {
    private final String id;
    private final long duration;
    private final long expiresBy;
    private final KeyType type;

    @ConstructorProperties({"id", "type", "duration", "expiresBy"})
    public Key(String id, KeyType type, long duration, long expiresBy) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.expiresBy = expiresBy;
    }

    public String getId() {
        return id;
    }

    public KeyType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public long getExpiresBy() {
        return expiresBy;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void save() {
        Bot.db().savePremiumKey(this);
    }

    @Override
    public void delete() {
        Bot.db().deleteKey(id);
    }
}
