package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import org.apache.commons.lang3.StringUtils
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("ignore"),
        usage = "(user|channel|role|list)",
        description = "Ignore users or channels.",
        ignorable = false,
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class IgnoreCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Ignore Management") {
                description {
                    buildString {
                        append("`user` • Ignore/unignore a user.").ln()
                        append("`channel` • Ignore/unignore a channel.").ln()
                        append("`role` • Ignore/unignore users with a role.").ln()
                        append("`list` • List ignored entities.")
                    }
                }
            }.action().queue()
            return
        }

        when (args[0]) {
            "user" -> {
                val member: Member?

                if (args.size < 2) {
                    context.send().error("Please mention a valid user. ie: `_ignore user Troll`").queue()
                    return
                } else {
                    val mentioned = context.message.mentionedUsers
                    if (mentioned.isNotEmpty()) {
                        member = context.guild.getMember(mentioned[0])
                    } else {
                        val name = StringUtils.join(args.copyOfRange(1, args.size), " ")
                        member = context.guild.getMembersByName(name, true).firstOrNull() ?:
                                context.guild.getMembersByNickname(name, true).firstOrNull()
                    }
                }

                if (member == null) {
                    context.send().error("You did not mention a valid user.").queue()
                    return
                }

                if (!context.member.canInteract(member)) {
                    context.send().error("You can not interact with this user.").queue()
                    return
                }

                context.guildOptions.ignoredUsers.let {
                    if (it.contains(member.user.id)) {
                        it.remove(member.user.id)

                        context.send().embed("Ignore") {
                            description {
                                "No longer ignoring user ${member.asMention}."
                            }
                        }.action().queue()
                    } else {
                        it.add(member.user.id)

                        context.send().embed("Ignore") {
                            description {
                                "Ignored user ${member.asMention}."
                            }
                        }.action().queue()
                    }
                }
                context.guildOptions.save()
            }
            "channel" -> {
                val channel: TextChannel

                if (args.size < 2) {
                    context.send().error("Please mention a channel. ie: `_ignore channel general`").queue()
                    return
                } else {
                    val mentioned = context.message.mentionedChannels
                    if (!mentioned.isEmpty()) {
                        channel = mentioned[0]
                    } else {
                        val name = StringUtils.join(args.copyOfRange(1, args.size), " ")
                        val channels = context.guild.getTextChannelsByName(name, true)
                        if (channels.isEmpty()) {
                            context.send().error("You did not mention a valid channel.").queue()
                            return
                        }
                        channel = channels[0]
                    }
                }

                context.guildOptions.ignoredChannels.let {
                    if (it.contains(channel.id)) {
                        it.remove(channel.id)

                        context.send().embed("Ignore") {
                            description {
                                "No longer ignoring channel ${channel.asMention}."
                            }
                        }.action().queue()
                    } else {
                        it.add(channel.id)

                        context.send().embed("Ignore") {
                            description {
                                "Ignored channel ${channel.asMention}."
                            }
                        }.action().queue()
                    }
                }
                context.guildOptions.save()
            }
            "role" -> {
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

                context.guildOptions.ignoredRoles.let {
                    if (it.contains(role.id)) {
                        it.remove(role.id)

                        context.send().embed("Ignore") {
                            description {
                                "No longer ignoring role ${role.asMention}."
                            }
                        }.action().queue()
                    } else {
                        it.add(role.id)

                        context.send().embed("Ignore") {
                            description {
                                "Ignored role ${role.asMention}."
                            }
                        }.action().queue()
                    }
                }
                context.guildOptions.save()
            }
            "list" -> {
                context.send().embed("Ignored Entities") {
                    field("Users") {
                        buildString {
                            context.guildOptions.ignoredUsers.let {
                                if (it.isEmpty()) {
                                    append("None of the users are ignored.")
                                } else it.forEach {
                                    append(context.guild.getMemberById(it).asMention)
                                }
                            }
                        }
                    }
                    field("Channel") {
                        buildString {
                            context.guildOptions.ignoredChannels.let {
                                if (it.isEmpty()) {
                                    append("None of the channels are ignored.")
                                } else it.forEach {
                                    append(context.guild.getTextChannelById(it).asMention)
                                }
                            }
                        }
                    }
                    field("Roles") {
                        buildString {
                            context.guildOptions.ignoredRoles.let {
                                if (it.isEmpty()) {
                                    append("None of the roles are ignored.")
                                } else it.forEach {
                                    append(context.guild.getRoleById(it).asMention)
                                }
                            }
                        }
                    }
                }.action().queue()
            }
            else -> {
                context.send().error("Invalid argument. Try `user`, `channel`, `role`, or `list` instead.").queue()
            }
        }
    }
}
