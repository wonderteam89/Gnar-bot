package xyz.gnarbot.gnar.commands.executors.`fun`

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.util.*

@Command(
        aliases = arrayOf("dialog"),
        usage = "(words...)",
        description = "Make some of that Windows ASCII art!"
)
class DialogCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val lines = WordUtils
                .wrap(StringUtils.join(args, ' ').replace("```", ""), 25, null, true)
                .split("\n")

        context.send().embed {
            description {
                buildString {
                    appendln("```")
                    appendln("╔═══════════════════════════╗ ")
                    appendln("║ Alert                     ║")
                    appendln("╠═══════════════════════════╣")

                    lines.map(String::trim)
                            .map {
                                it + buildString {
                                    kotlin.repeat(25 - it.length) { append(' ') }
                                }
                            }
                            .map { "║ $it ║" }
                            .forEach { appendln(it) }

                    when (Random().nextInt(4)) {
                        0 -> {
                            appendln("║  ┌─────────┐  ┌────────┐  ║")
                            appendln("║  │   Yes   │  │   No   │  ║")
                            appendln("║  └─────────┘  └────────┘  ║")
                        }
                        1 -> {
                            appendln("║ ┌─────┐  ┌──────┐  ┌────┐ ║")
                            appendln("║ │ Yes │  │ Help │  │ No │ ║")
                            appendln("║ └─────┘  └──────┘  └────┘ ║")
                        }
                        2 -> {
                            appendln("║  ┌─────────────────┬───┐  ║")
                            appendln("║  │     Confirm     │ X │  ║")
                            appendln("║  └─────────────────┴───┘  ║")
                        }
                        3 -> {
                            appendln("║  ┌───────────┐ ┌───────┐  ║")
                            appendln("║  │ HELLA YES │ │ PUSSY │  ║")
                            appendln("║  └───────────┘ └───────┘  ║")
                        }
                    }

                    appendln("╚═══════════════════════════╝")
                    appendln("```")
                }
            }
        }.action().queue()
    }
}