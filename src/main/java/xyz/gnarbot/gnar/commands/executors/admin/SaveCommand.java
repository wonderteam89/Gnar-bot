package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "save",
        admin = true,
        category = Category.NONE,
        ignorable = false
)
public class SaveCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        boolean clear = false;
        if (args.length > 0) {
            clear = Boolean.parseBoolean(args[0]);
        }
        context.send().info("Saved data to database.").queue();
    }
}
