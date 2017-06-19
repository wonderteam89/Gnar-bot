package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("pause"),
        description = "Pause or resume the music player.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class PauseCommand : CommandExecutor() {
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

        if (manager.player.playingTrack == null) {
            context.send().error("Can not pause or resume player because there is no track loaded for playing.").queue()
            return
        }

        manager.player.isPaused = !manager.player.isPaused

        context.send().embed("Playback Control") {
            setColor(Bot.CONFIG.musicColor)
            description {
                if (manager.player.isPaused) {
                    "The player has been paused."
                } else {
                    "The player has resumed playing."
                }
            }
        }.action().queue()
    }
}
