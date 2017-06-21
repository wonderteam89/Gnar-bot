package xyz.gnarbot.gnar.options;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;

import java.util.concurrent.TimeUnit;

public class OptionsRegistry {
    private TLongObjectMap<GuildOptions> registry = new TLongObjectHashMap<>();

    public OptionsRegistry() {
        Bot.EXECUTOR.scheduleAtFixedRate(this::save, 10, 30, TimeUnit.MINUTES);
    }

    public GuildOptions ofGuild(long id) {
        GuildOptions options = registry.get(id);

        if (options == null) {
            options = Bot.db().getGuildOptions(Long.toUnsignedString(id));

            if (options == null) {
                options = new GuildOptions(Long.toUnsignedString(id));
            }

            registry.put(id, options);
        }

        return options;
    }

    public void save() {
        for (GuildOptions options : registry.valueCollection()) {
            options.save();
        }
        registry.clear();
    }

    public GuildOptions ofGuild(Guild guild) {
        return ofGuild(guild.getIdLong());
    }

    public void delete(long id) {
        Bot.db().deleteGuildOptions(Long.toUnsignedString(id));
    }

    public void delete(Guild guild) {
        delete(guild.getIdLong());
    }
}
