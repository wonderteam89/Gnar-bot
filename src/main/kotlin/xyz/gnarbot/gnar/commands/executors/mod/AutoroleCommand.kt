package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("ignore"),
        usage = "(set|unset)",
        description = "Ignore users or channels.",
        ignorable = false,
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class AutoroleCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Autoroles") {
                description {
                    buildString {
                        append("`set` • Set the autorole.").ln()
                        append("`unset` • Unset the autorole.")
                    }
                }
            }.action().queue()
            return
        }

        when (args[0]) {

            else -> {
                context.send().error("Invalid argument. Try `set` or `unset` instead.").queue()
            }
        }

        context.guildOptions.save()
    }
}
