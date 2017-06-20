package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.util.HashMap;
import java.util.Map;

@Command(aliases = "leet", usage = "(string)", description = "Leet a string!", category = Category.FUN)
public class LeetifyCommand extends CommandExecutor {
    private final Map<String, String> substitutions = new HashMap<>();

    public LeetifyCommand() {
        substitutions.put("a", "4");
        substitutions.put("A", "@");
        substitutions.put("G", "6");
        substitutions.put("e", "3");
        substitutions.put("l", "1");
        substitutions.put("s", "5");
        substitutions.put("S", "$");
        substitutions.put("o", "0");
        substitutions.put("t", "7");
        substitutions.put("i", "!");
        substitutions.put("I", "1");
        substitutions.put("B", "|3");
    }

    @Override
    public void execute(Context context, String[] args) {
        String s = StringUtils.join(args, " ");

        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            s = s.replaceAll(entry.getKey(), entry.getValue());
        }

        context.send().embed("Leet it")
                .setDescription(s)
                .action().queue();
    }
}
