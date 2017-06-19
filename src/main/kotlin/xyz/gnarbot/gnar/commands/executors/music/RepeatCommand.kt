package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("repeat"),
        usage = "(song, playlist, none)",
        description = "Set if the music player should repeat.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RepeatCommand : CommandExecutor() {
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

        if (args.isEmpty()) {
            context.send().error("Please input one of these valid options `${RepeatOption.values().joinToString()}`").queue()
            return
        }

        val option = try {
            RepeatOption.valueOf(args[0].toUpperCase())
        } catch (e: IllegalArgumentException) {
            context.send().error("Valid options are `${RepeatOption.values().joinToString()}`").queue()
            return
        }

        manager.scheduler.repeatOption = option

        context.send().embed("Repeat Queue") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("\uD83D\uDD01 Music player was set to __**${manager.scheduler.repeatOption}**__.")
        }.action().queue()
    }
}
