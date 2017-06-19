package xyz.gnarbot.gnar.db;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.options.GuildOptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.*;

import static com.rethinkdb.RethinkDB.r;

public class Database {
    public static final Logger LOG = LoggerFactory.getLogger("Database");
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> task;
    private final Connection conn;
    private final String name;

    public Database(String name) {
        Connection conn = null;
        try {
            LOG.info("Connecting to database at " + Bot.KEYS.getDbAddr() + ":" + Bot.KEYS.getDbPort());
            if (Bot.KEYS.getDbPass().isEmpty()) {
                LOG.info("Connecting without password.");
                conn = r.connection().hostname(Bot.KEYS.getDbAddr()).port(Bot.KEYS.getDbPort()).connect();
            } else {
                LOG.info("Connecting using user: \"" + Bot.KEYS.getDbUser() + "\", pass: \"" + Bot.KEYS.getDbPass() + "\"");
                conn = r.connection().hostname(Bot.KEYS.getDbAddr()).port(Bot.KEYS.getDbPort()).user(Bot.KEYS.getDbUser(), Bot.KEYS.getDbPass()).connect();
            }
            if (r.dbList().<List<String>>run(conn).contains(name)) {
                LOG.info("Connected to database.");
            } else {
                LOG.info("Database of " + name + " is not present. Closing connection.");
                close();
            }
        } catch (ReqlDriverError e) {
            LOG.error("Database connection failed. " + e.getMessage());
        }
        this.conn = conn;
        this.name = name;

        task = isOpen() ? exec.scheduleAtFixedRate(() -> pushToDatabase(true), 1, 1, TimeUnit.HOURS) : null;
    }

    public boolean isOpen() {
        return conn != null && conn.isOpen();
    }

    public void close() {
        conn.close();
        if (task != null) task.cancel(false);
    }

    @Nullable
    public GuildOptions getGuildOptions(String id) {
        return isOpen() ? r.db(name).table("guilds").get(id).run(conn, GuildOptions.class) : null;
    }

    public void pushToDatabase(boolean free) {
        LOG.info("Pushing to database.");
        Bot.getOptionRegistry().save();
        if (free) {
            Bot.getOptionRegistry().clear();
        }
    }

    public void saveGuildOptions(GuildOptions guildData) {
        if (isOpen()) r.db(name).table("guilds").insert(guildData)
                .optArg("conflict", "replace")
                .runNoReply(conn);
    }

    public void deleteGuildOptions(String id) {
        if (isOpen()) r.db(name).table("guilds").get(id)
                .delete()
                .runNoReply(conn);
    }

    public ScheduledExecutorService getExecutor() {
        return exec;
    }

    public void queue(Callable<?> action) {
        getExecutor().submit(action);
    }

    public void queue(Runnable runnable) {
        getExecutor().submit(runnable);
    }
}
