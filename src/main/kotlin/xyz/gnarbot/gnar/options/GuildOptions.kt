package xyz.gnarbot.gnar.options

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.db.ManagedObject
import java.beans.ConstructorProperties

data class GuildOptions @ConstructorProperties("id") constructor(val id: String): ManagedObject {
    var disabledCommands: MutableSet<String> = hashSetOf()
    var ignoredChannels: MutableSet<String> = hashSetOf()
    var ignoredUsers: MutableSet<String> = hashSetOf()

    override fun save() = Bot.DATABASE.saveGuildOptions(this)
    override fun delete() = Bot.DATABASE.deleteGuildOptions(id)
}