package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Role
import org.apache.commons.lang3.StringUtils
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("autorole"),
        usage = "(set|unset)",
        description = "Set auto-roles, roles that are assigned to users on joining.",
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
                        append("`unset` • Unset the autorole.").ln()

                        val role = context.guildOptions.autoRole
                        append("Current auto-role: ")
                        if (role == null) {
                            append("__None__")
                        } else {
                            append(context.guild.getRoleById(role).asMention)
                        }
                    }
                }
            }.action().queue()
            return
        }

        when (args[0]) {
            "set" -> {
                val role: Role

                if (args.size < 2) {
                    context.send().error("Please mention a role. ie: `_ignore role Mod`").queue()
                    return
                } else {
                    val mentioned = context.message.mentionedRoles
                    if (!mentioned.isEmpty()) {
                        role = mentioned[0]
                    } else {
                        val name = StringUtils.join(args.copyOfRange(1, args.size), " ")
                        val roles = context.guild.getRolesByName(name, true)
                        if (roles.isEmpty()) {
                            context.send().error("You did not mention a valid role.").queue()
                            return
                        }
                        role = roles[0]
                    }
                }

                if (role == context.guild.publicRole) {
                    context.send().error("You can't grant the public role!").queue()
                    return
                }

                context.guildOptions.autoRole = role.id

                context.send().embed("Ignore") {
                    description {
                        "Users joining the channel will now be granted the role ${role.asMention}."
                    }
                }.action().queue()

                context.guildOptions.save()
            }
            "unset" -> {
                context.guildOptions.autoRole = null

                context.send().embed("Ignore") {
                    description {
                        "Unset autorole. Users joining the channel will not be granted any role."
                    }
                }.action().queue()

                context.guildOptions.save()
            }
            else -> {
                context.send().error("Invalid argument. Try `set` or `unset` instead.").queue()
            }
        }
    }
}
