package xyz.gnarbot.gnar.db;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.options.GuildOptions;

import javax.annotation.Nullable;
import java.util.List;

import static com.rethinkdb.RethinkDB.r;

public class Database {
    public static final Logger LOG = LoggerFactory.getLogger("Database");
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
                LOG.error("Database of " + name + " is not present. Closing connection.");
                close();
            }
        } catch (ReqlDriverError e) {
            LOG.error("Database connection failed. " + e.getMessage());
        }
        this.conn = conn;
        this.name = name;
    }

    public boolean isOpen() {
        return conn != null && conn.isOpen();
    }

    public void close() {
        conn.close();
    }

    @Nullable
    public GuildOptions getGuildOptions(String id) {
        return isOpen() ? r.db(name).table("guilds").get(id).run(conn, GuildOptions.class) : null;
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

    public Key getPremiumKey(String id) {
        return isOpen() ? r.db(name).table("keys").get(id).run(conn, Key.class) : null;
    }

    public void savePremiumKey(Key key) {
        if (isOpen()) r.db(name).table("keys").insert(key)
                .runNoReply(conn);
    }

    public void deleteKey(String id) {
        if (isOpen()) r.db(name).table("keys").get(id)
                .delete()
                .runNoReply(conn);
    }
}
