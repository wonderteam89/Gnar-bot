package xyz.gnarbot.gnar.options;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.Database;

public class OptionsRegistry {
    private final TLongObjectMap<GuildOptions> guildCache = new TLongObjectHashMap<>();

    public TLongObjectMap<GuildOptions> getGuildCache() {
        return guildCache;
    }

    public GuildOptions ofGuild(long id) {
        GuildOptions options = guildCache.get(id);

        if (options == null) {
            options = Bot.DATABASE.getGuildOptions(Long.toUnsignedString(id));
            if (options == null) {
                options = new GuildOptions(Long.toUnsignedString(id));
            } else {
                Database.LOG.debug("Loaded " + options + " from database.");
                options.getDisabledCommands().removeIf(it -> !Bot.getCommandRegistry().getCommandMap().containsKey(it));
            }
            guildCache.put(id, options);
        }

        return options;
    }

    public GuildOptions ofGuild(Guild guild) {
        return ofGuild(guild.getIdLong());
    }

    public void delete(long id) {
        Bot.DATABASE.deleteGuildOptions(Long.toUnsignedString(id));
        guildCache.remove(id);
    }

    public void delete(Guild guild) {
        delete(guild.getIdLong());
    }

    public void save() {
        for (GuildOptions options : guildCache.valueCollection()) {
            options.save();
        }
    }

    public void clear() {
        guildCache.clear();
    }
}
