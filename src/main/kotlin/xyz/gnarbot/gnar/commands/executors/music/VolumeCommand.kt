package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.link

@Command(
        aliases = arrayOf("volume"),
        description = "Set the volume of the music player.",
        usage = "(loudness %)",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class VolumeCommand : CommandExecutor() {
    private val totalBlocks = 20

    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayerRegistry().getExisting(context.guild)

        if (manager == null) {
            context.send().error("The player is not currently playing anything in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` in a channel to start playing some music!").queue()
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

        if (args.isEmpty()) {
            context.send().embed("Music Volume") {
                setColor(Bot.CONFIG.musicColor)

                field("", true) {
                    val percent = manager.player.volume.toDouble() / 100
                    buildString {
                        for (i in 0 until totalBlocks) {
                            if (i / totalBlocks.toDouble() > percent) {
                                append("\u25AC")
                            } else {
                                append("\u25AC" link "")
                            }
                        }
                        append(" **%.0f**%%".format(percent * 100))
                    }
                }

                setFooter("Set the volume by using _${info.aliases[0]} ${info.usage}.", null)
            }.action().queue()

            return
        }

        val amount = args[0].toDoubleOrNull()?.toInt()?.coerceIn(0, 100) ?: kotlin.run {
            context.send().error("Volume must be an integer.").queue()
            return
        }

        val old = manager.player.volume

        manager.player.volume = amount

        context.send().embed("Music Volume") {
            setColor(Bot.CONFIG.musicColor)

            description {
                val percent = amount.toDouble() / 100
                buildString {
                    for (i in 0 until totalBlocks) {
                        if (i / totalBlocks.toDouble() > percent) {
                            append("\u25AC")
                        } else {
                            append("\u25AC" link "")
                        }
                    }
                    append(" **%.0f**%%".format(percent * 100))
                }
            }

            setFooter("Volume changed from $old% to $amount%.", null)
        }.action().queue()
    }
}