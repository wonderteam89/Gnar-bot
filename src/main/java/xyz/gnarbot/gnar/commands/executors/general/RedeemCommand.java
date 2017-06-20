package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.db.Key;
import xyz.gnarbot.gnar.utils.Context;

import java.awt.*;
import java.time.Instant;
import java.util.Date;

@Command(
        aliases = "redeem",
        description = "Redeem a premium key."
)
public class RedeemCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("Please provide a redeemable code.").queue();
            return;
        }

        String id = args[0];

        Key key = Bot.db().getPremiumKey(id);

        if (key != null) {
            if (System.currentTimeMillis() > key.getExpiresBy()) {
                context.send().error("That code has expired.").queue();
                key.delete();
                return;
            }
            switch (key.getType()) {
                case PREMIUM:
                    context.getGuildOptions().addPremium(key.getDuration());
                    Bot.db().deleteKey(id);
                    context.send().embed("Premium Code")
                            .setColor(Color.ORANGE)
                            .setDescription("Redeemed key `" + key + "`. **Thank you for supporting the bot's development!**\n")
                            .appendDescription("Your **Premium** status will be valid until `" + Date.from(Instant.ofEpochMilli(context.getGuildOptions().getPremiumUntil())) + "`.")
                            .field("Donator Perks", true, () ->
                                    " • `_volume` Change the volume of the music player!\n"
                                            + " • `_soundcloud` Search SoundCloud!\n"
                                            + " • `_seek` Change the music player's position marker!\n")
                            .action().queue();
                    break;
                default:
                    context.send().error("Unknown key type.").queue();
            }
        } else {
            context.send().error("That is not a valid code.").queue();
        }
    }
}
