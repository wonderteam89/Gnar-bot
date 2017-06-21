package xyz.gnarbot.gnar.commands.executors.music.search

import com.jagrosh.jdautilities.menu.SelectorBuilder
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.link
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("youtube", "yt"),
        usage = "(query...)",
        description = "Search and see YouTube results.",
        scope = xyz.gnarbot.gnar.commands.Scope.TEXT,
        category = xyz.gnarbot.gnar.commands.Category.MUSIC
)
class YoutubeCommand : CommandExecutor() {
    override fun execute(context: xyz.gnarbot.gnar.utils.Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Input a query to search YouTube.").queue()
            return
        }

        val query = args.joinToString(" ")

        MusicManager.search("ytsearch:$query", 5) { results ->
            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return@search
            }

            val botChannel = context.guild.selfMember.voiceState.channel
            val userChannel = context.guild.getMember(context.message.author).voiceState.channel

            if (!Bot.CONFIG.musicEnabled || userChannel == null || botChannel != null && botChannel != userChannel) {
                context.send().embed {
                    setAuthor("YouTube Results", "https://www.youtube.com", "https://www.youtube.com/favicon.ico")
                    setThumbnail("https://gnarbot.xyz/assets/img/youtube.png")
                    setColor(java.awt.Color(141, 20, 0))

                    description {
                        buildString {
                            for (result in results) {

                                val title = result.info.title
                                val url = result.info.uri
                                val length = Utils.getTimestamp(result.duration)
                                val author = result.info.author

                                append(xyz.gnarbot.gnar.utils.b(title link url)).ln()
                                append("**`").append(length).append("`** by **").append(author).append("**").ln()
                            }
                        }
                    }

                    setFooter("Want to play one of these music tracks? Join a voice channel and reenter this command.", null)
                }.action().queue()
                return@search
            } else {
                SelectorBuilder(Bot.getWaiter()).apply {
                    setTitle("YouTube Results")
                    setDescription("Select one of the following options to play them in your current music channel.")
                    setColor(java.awt.Color(141, 20, 0))

                    setUser(context.user)

                    for (result in results) {
                        addOption("`${Utils.getTimestamp(result.info.length)}` ${b(result.info.title link result.info.uri)}") {
                            if (context.member.voiceState.inVoiceChannel()) {
                                val manager = Bot.getPlayerRegistry().get(context.guild)
                                manager.loadAndPlay(context, result.info.uri)
                            } else {
                                context.send().error("You're not in a voice channel anymore!").queue()
                            }
                        }
                    }
                }.build().display(context.channel)
            }
        }
    }
}



