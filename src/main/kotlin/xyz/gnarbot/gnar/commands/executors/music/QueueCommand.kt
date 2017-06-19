package xyz.gnarbot.gnar.commands.executors.music

import com.jagrosh.jdautilities.menu.PaginatorBuilder
import net.dv8tion.jda.core.entities.Member
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = arrayOf("queue", "list"),
        description = "Shows the music that's currently queued.",
        category = Category.MUSIC
)
class QueueCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayerRegistry().getExisting(context.guild)
        if (manager == null) {
            context.send().error("The player is not currently playing anything in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` in a channel to start playing some music!").queue()
            return
        }

        val queue = manager.scheduler.queue

        PaginatorBuilder(Bot.getWaiter())
                .setTitle("Music Queue")
                .setColor(Bot.CONFIG.musicColor)
                .apply {
                    if (queue.isEmpty()) {
                        add("**Empty queue.** Add some music with `_play url|YT search`.")
                    } else for (track in queue) {
                        add("`[${Utils.getTimestamp(track.duration)}]` __[${track.info.title}](${track.info.uri})__ from ${track.getUserData(Member::class.java).asMention}")
                    }
                }
                .build().display(context.channel)
    }
}
