package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "shutdown",
        admin = true,
        category = Category.NONE
)
public class ShutdownCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        for (Shard s : Bot.getShards()) {
            s.getJda().shutdown(true);
        }
        System.exit(21);
    }
}
