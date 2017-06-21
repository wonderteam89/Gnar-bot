package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.commands.LoadState;
import xyz.gnarbot.gnar.db.Database;
import xyz.gnarbot.gnar.listeners.BotListener;
import xyz.gnarbot.gnar.listeners.GuildCountListener;
import xyz.gnarbot.gnar.music.PlayerRegistry;
import xyz.gnarbot.gnar.options.OptionsRegistry;
import xyz.gnarbot.gnar.utils.DiscordLogBack;
import xyz.gnarbot.gnar.utils.SimpleLogToSLF4JAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");
    public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static final Credentials KEYS = new Credentials(new File("credentials.conf"));
    public static final BotConfiguration CONFIG = new BotConfiguration(new File("bot.conf"));
    private static final Database DATABASE = new Database(KEYS.getDbName());

    protected static final GuildCountListener guildCountListener = new GuildCountListener();
    protected static final BotListener botListener = new BotListener();
    protected static final EventWaiter waiter = new EventWaiter();

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final PlayerRegistry playerRegistry = new PlayerRegistry();
    private static final OptionsRegistry optionRegistry = new OptionsRegistry();

    private static final List<Shard> shards = new ArrayList<>();

    public static LoadState STATE = LoadState.LOADING;

//    private static MyAnimeListAPI malAPI;

    public static void main(String[] args) {
        SimpleLogToSLF4JAdapter.install();
        DiscordLogBack.enable();

        LOG.info("Initializing the Discord bot.");

//        LOG.info("Preparing for MAL with Credentials: " + KEYS.getMalUser() + " and " + KEYS.getMalPass());
//        malAPI = new MyAnimeListAPI(KEYS.getMalUser(), KEYS.getMalPass());

        LOG.info("Name:\t" + CONFIG.getName());
        LOG.info("JDAs:\t" + KEYS.getShards());
        LOG.info("Prefix:\t" + CONFIG.getPrefix());
        LOG.info("Admins:\t" + CONFIG.getAdmins().size());
        LOG.info("JDA:\t\t" + JDAInfo.VERSION);

        for (int i = 0; i < KEYS.getShards(); i++) {
            Shard shard = new Shard(i);
            shards.add(shard);
            shard.build();
        }

        STATE = LoadState.COMPLETE;

        for (Shard shard : shards) {
            shard.getJda().getPresence().setGame(Game.of(String.format(CONFIG.getGame(), shard.getId())));
        }


        LOG.info("The bot is now fully connected to Discord.");
    }

    public static Database db() {
        return DATABASE;
    }

//    public static MyAnimeListAPI getMALAPI() {
//        return malAPI;
//    }

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    public static OptionsRegistry getOptionRegistry() {
        return optionRegistry;
    }


    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static List<Shard> getShards() {
        return shards;
    }

    public static Shard getShard(int index) {
        return shards.get(index);
    }

    public static Shard getShard(JDA jda) {
        return shards.get(jda.getShardInfo() != null ? jda.getShardInfo().getShardId() : 0);
    }

    public static Guild getGuild(long id) {
        int shardId = (int) ((id >> 22) % Bot.KEYS.getShards());
        Shard shard = getShard(shardId);
        return shard != null ? shard.getJda().getGuildById(id) : null;
    }

    public static void restart() {
        LOG.info("Restarting the Discord bot shards.");
        for (Shard shard : shards) {
            shard.revive();
        }
        LOG.info("Discord bot shards have now restarted.");
    }
}
