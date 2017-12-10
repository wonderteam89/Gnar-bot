package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

@Command(
        aliases = "ping",
        description = "Show the bot's current response time."
)
@BotInfo(
        id = 18
)
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        long time = System.currentTimeMillis();

        context.getTextChannel().sendTyping().queue(ignored -> {
            long ping = System.currentTimeMillis() - time;
            context.getTextChannel().sendMessageFormat("**REST**: %d ms\n**Websocket**: %d ms", ping, context.getJDA().getPing()).queue();
        });
    }
}
