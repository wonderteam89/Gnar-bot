package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.exceptions.RateLimitedException
import xyz.gnarbot.gnar.Bot.CONFIG
import javax.security.auth.login.LoginException

class Shard(val id: Int) {
    /** @return the amount of successful requests on this command handler. */
    @JvmField var requests = 0

    private val builder = JDABuilder(AccountType.BOT).apply {
        setToken(Bot.KEYS.token)
        if (Bot.KEYS.shards > 1) useSharding(id, Bot.KEYS.shards)
        setAutoReconnect(true)
        setAudioEnabled(true)
        setAudioSendFactory(NativeAudioSendFactory())
        addEventListener(Bot.guildCountListener, Bot.waiter, Bot.botListener)
        setEnableShutdownHook(true)
        setGame(Game.of(String.format(CONFIG.game, id)))
    }

    lateinit var jda: JDA

    fun build() {
        try {
            Bot.LOG.info("Building shard $id.")
            jda = builder.buildBlocking()
            jda.selfUser.manager.setName(CONFIG.name).queue()
        } catch (e: LoginException) {
            throw e
        } catch (e: InterruptedException) {
            throw e
        } catch (e: RateLimitedException) {
            throw e
        }
    }

    fun revive() {
        Bot.LOG.info("Reviving shard $id.")

        jda.removeEventListener(Bot.guildCountListener, Bot.waiter, Bot.botListener)
        jda.shutdown(false)

        build()
    }

    /**
     * @return The string representation of the shard.
     */
    override fun toString() = "Shard(id=$id, guilds=${jda.guilds.size})"
}
