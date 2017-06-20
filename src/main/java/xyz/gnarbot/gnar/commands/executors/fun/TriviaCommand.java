package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.TriviaQuestions;

@Command(
        aliases = "trivia",
        category = Category.FUN
)
public class TriviaCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (TriviaQuestions.isNotSetup()) {
            TriviaQuestions.init();
        }

        if (args.length > 0) {
            context.send().info(TriviaQuestions.getRandomQuestion()).queue();
        }
    }
}
