package xyz.gnarbot.gnar.db;

import xyz.gnarbot.gnar.Bot;

import java.beans.ConstructorProperties;

public class PremiumKey implements ManagedObject {
    private final String id;
    private final long duration;

    @ConstructorProperties({"id", "duration"})
    public PremiumKey(String id, long duration) {
        this.id = id;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void save() {
        Bot.DATABASE.savePremiumKey(this);
    }

    @Override
    public void delete() {
        Bot.DATABASE.deletePremiumKey(id);
    }
}
