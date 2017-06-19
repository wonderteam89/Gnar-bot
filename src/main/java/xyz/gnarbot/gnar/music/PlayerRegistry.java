package xyz.gnarbot.gnar.music;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;

import javax.annotation.Nullable;

public class PlayerRegistry {
    public static final Logger LOG = LoggerFactory.getLogger("PlayerRegistry");

    private TLongObjectMap<MusicManager> registry = new TLongObjectHashMap<>();

    public MusicManager get(long id) {
        Guild guild = Bot.getGuild(id);
        return guild != null ? get(guild) : null;
    }

    public MusicManager get(Guild guild) {
        MusicManager manager = registry.get(guild.getIdLong());

        if (manager == null) { // why not pass Long? just in case guild doesnt exist
            LOG.debug("Created music manager of guild " + guild.getIdLong());
            manager = new MusicManager(guild);
            registry.put(guild.getIdLong(), manager);
        }

        return manager;
    }

    public MusicManager getExisting(long id) {
        return registry.get(id);
    }

    @Nullable
    public MusicManager getExisting(Guild guild) {
        return getExisting(guild.getIdLong());
    }

    public void remove(long id) {
        LOG.debug("Removed music manager of guild " + id);
        registry.remove(id);
    }

    public void remove(Guild guild) {
        remove(guild.getIdLong());
    }

    public void destroy(long id) {
        MusicManager manager = registry.get(id);
        if (manager != null) {
            manager.destroy();
            // The manager will remove itself.
        }
    }

    public void destroy(Guild guild) {
        destroy(guild.getIdLong());
    }

    public boolean contains(long id) {
        return registry.containsKey(id);
    }

    public boolean contains(Guild guild) {
        return registry.containsKey(guild.getIdLong());
    }
}
