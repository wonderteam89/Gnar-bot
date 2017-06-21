package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import java.awt.Color
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
abstract class MenuBuilder<T: MenuBuilder<T>>(val waiter: EventWaiter) {
    companion object {
        @JvmStatic val DEFAULT_FINALLY: (Message?) -> Unit = { it?.delete()?.queue() }
    }

    protected var user: User? = null
    protected var title: String = "Menu"
    protected var description: String? = null
    protected var color: Color? = null
    protected var finally: (Message?) -> Unit = DEFAULT_FINALLY
    protected var timeout: Long = 20
    protected var unit: TimeUnit = TimeUnit.SECONDS

    fun setTitle(title: String): T {
        this.title = title
        return this as T
    }

    fun setDescription(description: String): T {
        this.description = description
        return this as T
    }

    fun setColor(color: Color): T {
        this.color = color
        return this as T
    }

    fun setUser(user: User): T {
        this.user = user
        return this as T
    }

    fun finally(action: (Message?) -> Unit) {
        this.finally = action
    }

    fun setTimeout(timeout: Long, unit: TimeUnit): T {
        this.timeout = timeout
        this.unit = unit
        return this as T
    }

    abstract fun build(): Any
}