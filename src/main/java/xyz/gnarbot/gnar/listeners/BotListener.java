package xyz.gnarbot.gnar.listeners;


import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.CommandDispatcher;
import xyz.gnarbot.gnar.commands.LoadState;
import xyz.gnarbot.gnar.options.GuildOptions;

public class BotListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            CommandDispatcher.INSTANCE.handleEvent(event);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            GuildOptions options = Bot.getOptionRegistry().ofGuild(event.getGuild());

            if (options.getAutoRole() != null) {
                Role role = event.getGuild().getRoleById(options.getAutoRole());
                event.getGuild().getController().addRolesToMember(event.getMember(), role).queue();
            }
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            Bot.getOptionRegistry().ofGuild(event.getGuild().getIdLong()).delete();
        }
    }

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            if (event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent) {
                if (event.getMember().getUser() == event.getJDA().getSelfUser()) return;

                Guild guild = event.getGuild();

                if (guild == null) return;

                VoiceChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

                VoiceChannel channelLeft;

                if (event instanceof GuildVoiceLeaveEvent) {
                    channelLeft = ((GuildVoiceLeaveEvent) event).getChannelLeft();
                } else {
                    channelLeft = ((GuildVoiceMoveEvent) event).getChannelLeft();
                }

                if (botChannel == null || !channelLeft.equals(botChannel)) return;

                if (botChannel.getMembers().size() == 1) {
                    Bot.getPlayerRegistry().destroy(guild);
                }
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        Bot.LOG.info("JDA " + (event.getJDA().getShardInfo() != null ? event.getJDA().getShardInfo().getShardId() : "instance") + " is ready.");
    }

    @Override
    public void onResume(ResumedEvent event) {
        Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has resumed.");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has reconnected.");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        if (event.isClosedByServer()) {
            Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has disconnected (closed by server). "
                    + "Code: " + event.getServiceCloseFrame().getCloseCode() + " "  + event.getCloseCode());
        } else {
            Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has disconnected. "
                    + "Code: " + event.getClientCloseFrame().getCloseCode() + " " + event.getClientCloseFrame().getCloseReason());
        }
    }

    @Override
    public void onException(ExceptionEvent event) {
        if (!event.isLogged()) Bot.LOG.error("Error thrown by JDA.", event.getCause());
    }
}
