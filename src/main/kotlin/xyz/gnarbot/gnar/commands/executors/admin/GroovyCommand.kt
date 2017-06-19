package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.awt.Color
import javax.script.*

@Command(
        aliases = arrayOf("gv"),
        description = "Run Groovy scripts.",
        admin = true,
        category = Category.NONE
)
class GroovyCommand : CommandExecutor() {
    companion object {
        val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("groovy")
    }

    override fun execute(context: Context, args: Array<String>) {
        val script = args.joinToString(" ")
        if (script.isNullOrEmpty()) {
            context.send().error("Script can not be empty.").queue()
            return
        }

        val scope = SimpleScriptContext()

        scope.getBindings(ScriptContext.ENGINE_SCOPE).apply {
            put("context", context)
            put("Bot", Bot::class.java)
        }

        context.send().embed("Groovy") {
            field("Running", false) { script }
            field("Result", false) {
                try {
                    scriptEngine.eval(script, scope).toString()
                } catch (e: ScriptException) {
                    setColor(Color.RED)
                    "The error `$e` occurred while executing the Groovy statement."
                }
            }
        }.action().queue()
    }

}