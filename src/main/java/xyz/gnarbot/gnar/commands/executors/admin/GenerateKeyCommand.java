package xyz.gnarbot.gnar.commands.executors.admin;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.db.PremiumKey;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

import java.util.Arrays;
import java.util.UUID;

@Command(
        aliases = "genkey",
        admin = true,
        category = Category.NONE,
        ignorable = false
)
public class GenerateKeyCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length < 2) {
            context.send().error("Insufficient args.");
            return;
        }

        int num;

        try {
            num = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            context.send().error("That's not a number.").queue();
            return;
        }

        if (num < 0) {
            context.send().error("Negative keys, are you drunk?").queue();
            return;
        }

        long duration = Utils.parseTimestamp(StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "));

        if (duration < 0) {
            context.send().error("Negative duration, we get it you vape.").queue();
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            PremiumKey key = new PremiumKey(UUID.randomUUID().toString(), duration);
            builder.append(key.getId()).append('\n');
            key.save();
        }
        context.getUser().openPrivateChannel().queue(it -> it.sendMessage(Utils.hasteBin(builder.toString())).queue());
    }
}