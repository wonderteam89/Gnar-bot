package xyz.gnarbot.gnar.options;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.Database;

public class OptionsRegistry {
    public GuildOptions ofGuild(long id) {
        GuildOptions options = Bot.db().getGuildOptions(Long.toUnsignedString(id));

        if (options == null) {
            options = new GuildOptions(Long.toUnsignedString(id));
        } else {
            Database.LOG.debug("Loaded " + options + " from database.");
        }

        return options;
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
