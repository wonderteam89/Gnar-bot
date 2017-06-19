package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.options.GuildOptions;

public final class Context {
    private final Message message;
    private final TextChannel channel;
    private final Guild guild;
    private final GuildOptions guildOptions;
    private final JDA jda;
    private final Shard shard;
    private final Member member;
    private final User user;

    public Context(GuildMessageReceivedEvent event) {
        this.message = event.getMessage();
        this.channel = event.getChannel();
        this.guild = event.getGuild();
        this.guildOptions = Bot.getOptionRegistry().ofGuild(this.guild);
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);
        this.member = event.getMember();
        this.user = event.getAuthor();
    }

    public final Message getMessage() {
        return this.message;
    }

    public final TextChannel getChannel() {
        return this.channel;
    }

    public final Guild getGuild() {
        return this.guild;
    }

    public final GuildOptions getGuildOptions() {
        return this.guildOptions;
    }

    public final JDA getJda() {
        return this.jda;
    }

    public final Shard getShard() {
        return this.shard;
    }

    public final Member getMember() {
        return this.member;
    }

    public final User getUser() {
        return this.user;
    }

    public final ResponseBuilder send() {
        return new ResponseBuilder(this);
    }
}
