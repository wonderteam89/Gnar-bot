package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("skip"),
        description = "Skip the current music track.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class SkipCommand : CommandExecutor() {
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

        if (!(manager.player.playingTrack.userData == context.member
                || context.member != null && context.member.hasPermission(botChannel, Permission.MANAGE_CHANNEL))) {
            context.send().error("You did not request this track.").queue()
            return
        }

        manager.scheduler.nextTrack()

        context.send().embed("Skip Current Track") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("The track was skipped.")
        }.action().queue()
    }
}
