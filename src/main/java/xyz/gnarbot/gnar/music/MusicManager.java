package xyz.gnarbot.gnar.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.utils.Context;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MusicManager {
    private static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    static {
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
    }

    public static void search(String query, int maxResults, Consumer<List<AudioTrack>> callback) {
        playerManager.loadItemOrdered(playerManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                callback.accept(Collections.singletonList(track));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!playlist.isSearchResult()) {
                    return;
                }

                callback.accept(playlist.getTracks().subList(0, Math.min(maxResults, playlist.getTracks().size())));
            }

            @Override
            public void noMatches() {
                callback.accept(Collections.emptyList());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                callback.accept(Collections.emptyList());
            }
        });
    }

    private final long id;
    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    private long lastVoteTime = 0;
    private boolean isVotingToSkip = false;

    public MusicManager(Guild guild) {
        this(guild.getIdLong());
    }

    private MusicManager(long guild) {
        this.id = guild;
        this.player = playerManager.createPlayer();
        this.scheduler = new TrackScheduler(this, player);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(player);
    }

    public void destroy() {
        closeAudioConnection();
        player.destroy();
        Bot.getPlayerRegistry().remove(id);
    }
    
    public Guild getGuild() {
        return Bot.getGuild(id);
    }

    public void openAudioConnection(VoiceChannel channel, Context context) {
        if (!Bot.CONFIG.getMusicEnabled()) {
            context.send().error("Music is disabled.").queue();
            return;
        }

        if (!getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            context.send().error("The bot can't connect to this channel due to a lack of permission.").queue();
            return;
        }

        getGuild().getAudioManager().setSendingHandler(sendHandler);
        getGuild().getAudioManager().openAudioConnection(channel);

        context.send().embed("Music Playback")
                .setColor(Bot.CONFIG.getMusicColor())
                .setDescription("Joined channel `" + channel.getName() + "`.")
                .action().queue();
    }

    public void closeAudioConnection() {
        getGuild().getAudioManager().closeAudioConnection();
        getGuild().getAudioManager().setSendingHandler(null);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public long getLastVoteTime() {
        return lastVoteTime;
    }

    public void setLastVoteTime(long lastVoteTime) {
        this.lastVoteTime = lastVoteTime;
    }

    public boolean isVotingToSkip() {
        return isVotingToSkip;
    }

    public void setVotingToSkip(boolean value) {
        this.isVotingToSkip = value;
    }

    public void loadAndPlay(Context context, String trackUrl) {
        loadAndPlay(context, trackUrl, null);
    }

    public void loadAndPlay(Context context, String trackUrl, String footnote) {
        Guild guild = getGuild();

        if (guild.getSelfMember().getVoiceState().getChannel() == null) {
            if (context.getMember().getVoiceState().getChannel() == null) {
                context.send().error("Error, not in a channel?").queue();
                return;
            }
            openAudioConnection(context.getMember().getVoiceState().getChannel(), context);
        }

        playerManager.loadItemOrdered(playerManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.getQueue().size() >= Bot.CONFIG.getQueueLimit()) {
                    context.send().error("The queue can not exceed " + Bot.CONFIG.getQueueLimit() + " songs.").queue();
                    return;
                }

                if (!(track instanceof TwitchStreamAudioTrack || track instanceof BeamAudioTrack)) {
                    if (track.getDuration() > Bot.CONFIG.getDurationLimit().toMillis()) {
                        context.send().error("The track can not exceed " + Bot.CONFIG.getDurationLimitText() + ".").queue();
                        return;
                    }
                }

                track.setUserData(context.getMember());

                scheduler.queue(track);

                context.send().embed("Music Queue")
                    .setColor(Bot.CONFIG.getMusicColor())
                    .setDescription("Added __**[" + track.getInfo().title + "](" + track.getInfo().uri + ")**__ to queue.")
                    .setFooter(footnote, null)
                    .action().queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                if (scheduler.getQueue().size() >= Bot.CONFIG.getQueueLimit()) {
                    context.send().error("The queue can not exceed " + Bot.CONFIG.getQueueLimit() + " songs.").queue();
                    return;
                }

                List<AudioTrack> tracks = playlist.getTracks();

                int added = 0;
                int ignored = 0;
                for (AudioTrack track : tracks) {
                    if (scheduler.getQueue().size() + 1 >= Bot.CONFIG.getQueueLimit()) {
                        ignored = tracks.size() - added;
                        break;
                    }

                    track.setUserData(context.getMember());

                    scheduler.queue(track);
                    added++;
                }

                context.send().embed("Music Queue")
                        .setColor(Bot.CONFIG.getMusicColor())
                        .setDescription("Added ` " + added + "` tracks to queue from playlist `" + playlist.getName() + "`.")
                        .appendDescription(ignored != 0 ? "" : "\nIgnored `" + ignored + "` tracks as it exceeded the queue limits.")
                        .setFooter(footnote, null)
                        .action().queue();
            }

            @Override
            public void noMatches() {
                context.send().error("Nothing matches for `" + trackUrl + "`.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                context.send().exception(exception).queue();
            }
        });
    }
}