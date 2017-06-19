package xyz.gnarbot.gnar.commands

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color

object CommandDispatcher {
    fun handleEvent(event: GuildMessageReceivedEvent) {
        if (!event.message.content.startsWith(Bot.CONFIG.prefix)) {
            return
        }

        if (event.author == null || event.member == null) {
            return
        }

        launch(CommonPool) {
            Context(event).let {
                if (callCommand(it)) {
                    it.shard.requests++
                }
            }

        }
    }

    /**
     * Call the command based on the message content.
     *
     * @return If the call was successful.
     */
    fun callCommand(context: Context) : Boolean {
        if (!context.guild.selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            context.send().text("The bot needs the `Embed Links` permission to show messages.")
                    .queue(Utils.deleteMessage(15))
            return false
        }

        if (context.guildOptions.ignoredUsers.contains(context.user.id)) {
            return false
        }

        val content = context.message.content
        if (!content.startsWith(Bot.CONFIG.prefix)) return false

        // Split the message.
        val tokens = Utils.stringSplit(content)

        val label = tokens[0].substring(Bot.CONFIG.prefix.length).toLowerCase().trim()

        if (label in context.guildOptions.disabledCommands) {
            context.send().error("This command is disabled by the server owner.").queue()
            return false
        }

        val cmd = Bot.getCommandRegistry().getCommand(label) ?: return false

        val isIgnored = context.guildOptions.ignoredChannels.contains(context.channel.id)

        if (cmd.info.ignorable && isIgnored) {
            return false
        }

        val args = tokens.copyOfRange(1, tokens.size)

        if (args.isNotEmpty() && (args[0] == "help" || args[0] == "?")) {
            if (!isIgnored) context.send().embed("Command Information") {
                field("Aliases", true) { cmd.info.aliases.joinToString(separator = ", ${Bot.CONFIG.prefix}", prefix = Bot.CONFIG.prefix) }
                field("Usage", true) { "${Bot.CONFIG.prefix}${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}" }
                if (cmd.info.donor) {
                    field("🌟 Donator", true) { "This command is exclusive to donators' guilds. Donate to our Patreon or PayPal to gain access to them." }
                } else {
                    field(true)
                }

                if (cmd.info.permissions.isNotEmpty()) {
                    field("Guild Permission", true) { "${cmd.info.scope} ${cmd.info.permissions.map(Permission::getName)}" }
                }

                field("Description") { cmd.info.description }
            }.action().queue()
            return true
        }

        val message = context.message
        val member = context.member

        if (member.user.idLong !in Bot.CONFIG.admins && cmd.info.admin) {
            if (!isIgnored) context.send().error("This command is for bot administrators only.").queue()
            return false
        }

        if (member.user.idLong !in Bot.CONFIG.admins && cmd.info.donor && !context.guildOptions.isPremium()) {
            if (!isIgnored) context.send().embed("Donators Only") {
                setColor(Color.ORANGE)
                description {
                    buildString {
                        append("🌟 This command is for donators' servers only.").ln()
                        append("In order to enjoy donator perks, please consider pledging to __**[our Patreon.](https://www.patreon.com/gnarbot)**__").ln()
                        append("Once you donate, join our __**[support guild](http://discord.gg/NQRpmr2)**__ and ask one of the owners.")
                    }
                }
            }.action().queue()
            return false
        }

        if (member.user.idLong !in Bot.CONFIG.admins && cmd.info.guildOwner) {
            if (context.guild.owner != member) {
                if (!isIgnored) context.send().error("This command is for server owners only.").queue()
                return false
            }
        }

        if (cmd.info.scope == Scope.VOICE) {
            if (member.voiceState.channel == null) {
                if (!isIgnored) context.send().error("\uD83C\uDFB6 Music commands requires you to be in a voice channel.").queue()
                return false
            }
        }

        if (member.user.idLong !in Bot.CONFIG.admins && cmd.info.permissions.isNotEmpty()) {
            if (!cmd.info.scope.checkPermission(context, *cmd.info.permissions)) {
                if (!isIgnored) {
                    val requirements = cmd.info.permissions.map(Permission::getName)
                    context.send().error("You lack the following permissions: `$requirements` in " + when (cmd.info.scope) {
                        Scope.GUILD -> "the guild `${message.guild.name}`."
                        Scope.TEXT -> "the text channel `${message.textChannel.name}`."
                        Scope.VOICE -> "the voice channel `${member.voiceState.channel.name}`."
                    }).queue()
                }
                return false
            }
        }


        try {
            cmd.execute(context, args)
            return true
        } catch (e: PermissionException) {
            context.send().error("The bot lacks the permission `${e.permission.getName()}` required to perform this command.").queue()
        } catch (e: RuntimeException) {
            context.send().exception(e).queue()
            e.printStackTrace()
        }
        return false
    }
}
