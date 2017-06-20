package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("cmd", "command", "cmds", "commands"),
        usage = "[commands...]",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR),
        ignorable = false
)
class ManageCommandsCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Command Management") {
                description {
                    buildString {
                        append("`enable` • Enable a command.").ln()
                        append("`disable` • Disable a command.").ln()
                        append("`list` • List disabled commands.").ln()
                    }
                }
            }.action().queue()
            return
        }

        when (args[0]) {
            "enable" -> {
                if (args.size != 2) {
                    context.send().error("Please input a command argument `ie. _cmd enable roll`.").queue()
                    return
                }
                val aliases = lookupCommand(args[1])?.info?.aliases

                if (aliases == null || aliases.isEmpty()) {
                    context.send().error("`${args[1]}` is not a valid command.").queue()
                    return
                }

                val list = aliases.filter(context.guildOptions.disabledCommands::contains)

                if (list.isEmpty()) {
                    context.send().error("`${args[1]}` is not disabled.").queue()
                    return
                }

                context.guildOptions.disabledCommands.removeAll(list)
                context.guildOptions.save()

                context.send().info("Enabling ${list.joinToString { "`$it`" }}.").queue()
            }
            "disable" -> {
                if (args.size != 2) {
                    context.send().error("Please input a command argument `ie. _cmd disable roll`.").queue()
                    return
                }
                val info = lookupCommand(args[1])?.info

                if (info == null) {
                    context.send().error("`${args[1]}` is not a valid command.").queue()
                    return
                } else if (!info.toggleable) {
                    context.send().error("`${args[1]}` can not be toggled.").queue()
                    return
                }

                val aliases = info.aliases

                if (aliases.isEmpty()) {
                    context.send().error("`${args[1]}` is not a valid command.").queue()
                    return
                }

                val list = aliases.filterNot(context.guildOptions.disabledCommands::contains)

                if (list.isEmpty()) {
                    context.send().error("`${args[1]}` is already disabled.").queue()
                    return
                }

                context.guildOptions.disabledCommands.addAll(list)
                context.guildOptions.save()

                context.send().info("Disabling ${list.joinToString { "`$it`" }}.").queue()
            }
            "list" -> {
                context.send().embed("Disabled Commands") {
                    description {
                        if (context.guildOptions.disabledCommands.isEmpty()) {
                            "No commands disabled!? Hooray!"
                        } else buildString {
                            context.guildOptions.disabledCommands.forEach {
                                append("• `").append(it).append("`\n")
                            }
                        }
                    }
                }.action().queue()
            }
            else -> {
                context.send().error("Invalid argument. Try `enable`, `disable`, or `list` instead.").queue()
            }
        }

    }

    private fun lookupCommand(label: String): CommandExecutor? {
        return Bot.getCommandRegistry().getCommand(label)
    }
}