package xyz.gnarbot.gnar.options

import com.fasterxml.jackson.annotation.JsonIgnore
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.db.ManagedObject
import java.beans.ConstructorProperties
import java.lang.System.currentTimeMillis



data class GuildOptions @ConstructorProperties("id") constructor(val id: String): ManagedObject {
    var disabledCommands: MutableSet<String> = hashSetOf()
    var ignoredChannels: MutableSet<String> = hashSetOf()
    var ignoredUsers: MutableSet<String> = hashSetOf()
    var ignoredRoles: MutableSet<String> = hashSetOf()
    var autoRole: String? = null

    var premiumUntil: Long = 0

    @JsonIgnore
    fun isPremium() = currentTimeMillis() < premiumUntil

    @JsonIgnore
    fun remainingPremium(): Long {
        return if (isPremium()) this.premiumUntil - currentTimeMillis() else 0
    }

    @JsonIgnore
    fun addPremium(ms: Long) {
        when {
            isPremium() -> this.premiumUntil += ms
            else -> this.premiumUntil = currentTimeMillis() + ms
        }
    }

    override fun save() = Bot.db().saveGuildOptions(this)
    override fun delete() = Bot.db().deleteGuildOptions(id)
}