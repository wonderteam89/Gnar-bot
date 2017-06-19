package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("revive"),
        description = "Restart all Shard instances.",
        admin = true,
        category = Category.NONE
)
class RestartShardsCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Restarting Shards") {
                setDescription("Bot is now restarting.")
            }.action().queue()

            Bot.restart()
        } else {
            val id = args[0].toIntOrNull()?.coerceIn(0, Bot.getShards().size) ?: kotlin.run {
                context.send().error("You must enter a valid shard id.").queue()
                return
            }

            Bot.getShard(id).revive()
        }
    }
}