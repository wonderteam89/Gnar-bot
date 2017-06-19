package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("seek"),
        description = "Skip the current music track.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class SeekCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayerRegistry().getExisting(context.guild)

        if (manager == null) {
            context.send().error("The player is not currently playing anything in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n"
                    + "\uD83C\uDFB6 `_play (song/url)` to start playing some music!\n"
                    + "\uD83E\uDD16 The bot will automatically join your channel.").queue()
            return
        }
        if (botChannel != context.member.voiceState.channel) {
            context.send().error("You're not in the same channel as the bot.").queue()
            return
        }

        if (!(context.member.hasPermission(Permission.MANAGE_CHANNEL)
                || manager.player.playingTrack.userData == context.member)) {
            context.send().error("You did not request this track.").queue()
            return
        }

        if (manager.player.playingTrack == null) {
            context.send().error("You are not playing any music.").queue()
            return
        }

        if (!manager.player.playingTrack.isSeekable) {
            context.send().error("This track is not seekable.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().embed("Music Seeking") {
                color { Bot.CONFIG.musicColor }
                description {
                    buildString {
                        append("`forward (time)` • Add to the time marker.").ln()
                        append("`backward (time)` • Subtract from the time marker.").ln()
                        append("`(time)` • Set the time marker of the player.").ln()
                    }
                }
                footer { "Time arguments examples: 1 hour 2 seconds, 1m30s, 2min" }
            }.action().queue()
            return
        }

        when(args[0]) {
            "fwd", "add", "forward" -> {
                val query = args.copyOfRange(1, args.size).joinToString(" ")
                if (query.isBlank()) {
                    context.send().error("Please input a timestamp, ex: `1 hour 2 seconds` `1m30s` `2 minutes`.").queue()
                    return
                }

                val ms = Utils.parseTimestamp(query)
                if (ms < 0) {
                    context.send().error("Time value can not be negative.").queue()
                    return
                }

                manager.player.playingTrack.position = (manager.player.playingTrack.position + ms)
                        .coerceIn(0, manager.player.playingTrack.duration)
            }
            "bwd", "back", "subtract", "backwards" -> {
                val query = args.copyOfRange(1, args.size).joinToString(" ")
                if (query.isBlank()) {
                    context.send().error("Please input a timestamp, ex: `1 hour 2 seconds` `1m30s` `2 minutes`.").queue()
                    return
                }

                val ms = Utils.parseTimestamp(query)
                if (ms < 0) {
                    context.send().error("Time value can not be negative.").queue()
                    return
                }

                manager.player.playingTrack.position = (manager.player.playingTrack.position - ms)
                        .coerceIn(0, manager.player.playingTrack.duration)
            }
            else -> {
                val query = args.joinToString(" ")
                if (query.isBlank()) {
                    context.send().error("Please input a timestamp, ex: `1 hour 2 seconds` `1m30s` `2 minutes`.").queue()
                    return
                }

                val ms = Utils.parseTimestamp(query)
                if (ms < 0) {
                    context.send().error("Time value can not be negative.").queue()
                    return
                }

                manager.player.playingTrack.position = ms.coerceIn(0, manager.player.playingTrack.duration)
            }
        }

        context.send().embed("Seek Length") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}.")
        }.action().queue()
    }
}
