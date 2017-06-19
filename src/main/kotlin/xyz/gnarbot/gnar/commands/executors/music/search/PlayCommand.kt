package xyz.gnarbot.gnar.commands.executors.music.search

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("play"),
        usage = "-(url|YT search)",
        description = "Joins and play music in a channel.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    val footnote = "\uD83D\uDD0D You can search and pick results using _youtube or _soundcloud while in a channel."

    override fun execute(context: Context, args: Array<String>) {
        val botChannel = context.guild.selfMember.voiceState.channel
        val userChannel = context.member.voiceState.channel

        if (botChannel != null && botChannel != userChannel) {
            context.send().error("The bot is already playing music in another channel.").queue()
            return
        }

        if (args.isEmpty()) {
            val manager = Bot.getPlayerRegistry().getExisting(context.guild)

            if (manager == null) {
                context.send().error("The player is not currently playing anything in this guild.\n" +
                        "\uD83C\uDFB6` _play (song/url)` in a channel to start playing some music!").queue()
                return
            }

            if (manager.player.isPaused) {
                manager.player.isPaused = false
                context.send().embed("Play Music") {
                    setColor(Bot.CONFIG.musicColor)
                    setDescription("Music is now playing.")
                }.action().queue()
            } else if (manager.player.playingTrack != null) {
                context.send().error("Music is already playing.").queue()
            } else if (manager.scheduler.queue.isEmpty()) {
                context.send().embed("Empty Queue") {
                    setColor(Bot.CONFIG.musicColor)
                    setDescription("There is no music queued right now. Add some songs with `play -song|url`.")
                }.action().queue()
            }
            return
        }

        if ("https://" in args[0] || "http://" in args[0]) {
            val manager = Bot.getPlayerRegistry().get(context.guild)
            manager.loadAndPlay(context, args[0], footnote)
        } else {
            val query = args.joinToString(" ").trim()

            if (query.startsWith("ytsearch:", ignoreCase = true)) {
                Bot.getCommandRegistry().getCommand("youtube").execute(context, arrayOf(query.replaceFirst("ytsearch:", "", true)))
                return
            } else if (query.startsWith("scsearch:", ignoreCase = true)) {
                Bot.getCommandRegistry().getCommand("soundcloud").execute(context, arrayOf(query.replaceFirst("scsearch:", "", true)))
                return
            } else if (query.startsWith("youtube ", ignoreCase = true)) {
                Bot.getCommandRegistry().getCommand("youtube").execute(context, arrayOf(query.replaceFirst("youtube ", "", true)))
                return
            } else if (query.startsWith("soundcloud ", ignoreCase = true)) {
                Bot.getCommandRegistry().getCommand("soundcloud").execute(context, arrayOf(query.replaceFirst("soundcloud ", "", true)))
                return
            }

            MusicManager.search("ytsearch:$query", 1) { results ->
                if (results.isEmpty()) {
                    context.send().error("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
                    return@search
                }

                val result = results[0]

                val manager = Bot.getPlayerRegistry().get(context.guild)
                manager.loadAndPlay(context, result.info.uri, footnote)
            }
        }
    }
}
