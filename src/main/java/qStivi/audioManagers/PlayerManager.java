package qStivi.audioManagers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getINSTANCE() {

        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackURL) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                musicManager.trackScheduler.queue(tracks.get(0));
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }


        });
    }


    /**
     * Starts playing the next track in the queue.<br><br>
     * If the queue is empty the playback is going to be stopped.
     */
    public void skip(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.audioPlayer.startTrack(musicManager.trackScheduler.queue.poll(), false);
    }

    public void pause(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.audioPlayer.setPaused(true);
    }

    public void continueTrack(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.audioPlayer.setPaused(false);
    }

    public void setRepeat(TextChannel channel, boolean repeat) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.trackScheduler.isRepeating = repeat;
    }

    public boolean isRepeating(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        return musicManager.trackScheduler.isRepeating;
    }

    public void clearQueue(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.trackScheduler.queue.clear();
    }
}
