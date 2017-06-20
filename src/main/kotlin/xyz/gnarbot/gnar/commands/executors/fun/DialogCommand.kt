package xyz.gnarbot.gnar.commands.executors.`fun`

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

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

                    when ((Math.random() * 7).toInt()) {
                        0 -> {
                            append("║  ┌─────────┐  ┌────────┐  ║").ln()
                            append("║  │   Yes   │  │   No   │  ║").ln()
                            append("║  └─────────┘  └────────┘  ║").ln()
                        }
                        1 -> {
                            append("║ ┌─────┐  ┌──────┐  ┌────┐ ║").ln()
                            append("║ │ Yes │  │ Help │  │ No │ ║").ln()
                            append("║ └─────┘  └──────┘  └────┘ ║").ln()
                        }
                        2 -> {
                            append("║  ┌─────────────────┬───┐  ║").ln()
                            append("║  │     Confirm     │ X │  ║").ln()
                            append("║  └─────────────────┴───┘  ║").ln()
                        }
                        3 -> {
                            append("║  ┌──────────┬──────────┐  ║").ln()
                            append("║  │   Accept │ Decline  │  ║").ln()
                            append("║  └──────────┴──────────┘  ║").ln()
                        }
                        4 -> {
                            append("║  ┌─────────┐ ┌─────────┐  ║").ln()
                            append("║  │   Yes   │ │   Yes   │  ║").ln()
                            append("║  └─────────┘ └─────────┘  ║").ln()
                        }
                        5 -> {
                            append("║  ┌────┐   ┌────┐   ┌────┐ ║").ln()
                            append("║  │ No │   │ No │   │ No │ ║").ln()
                            append("║  └────┘   └────┘   └────┘ ║").ln()
                        }
                        6 -> {
                            append("║  ┌───────────┐ ┌───────┐  ║").ln()
                            append("║  │ HELLA YES │ │ PUSSY │  ║").ln()
                            append("║  └───────────┘ └───────┘  ║").ln()
                        }
                    }

                    appendln("╚═══════════════════════════╝")
                    appendln("```")
                }
            }
        }.action().queue()
    }
}