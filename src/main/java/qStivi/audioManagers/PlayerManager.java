package qStivi.audioManagers;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.remote.RemoteNodeRegistry;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.DecodedTrackHolder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

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

    public void loadAndPlay(TextChannel channel, Guild guild, String trackURL) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

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
     *
     * @param guild is the guild where the music should be skipped.
     */
    public void skip(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        musicManager.audioPlayer.startTrack(musicManager.trackScheduler.queue.poll(), false);
    }

    /**
     * Pauses the currently playing track.
     *
     * @param guild is the guild where the music should be paused.
     */
    public void pause(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        musicManager.audioPlayer.setPaused(true);
    }

    /**
     * Continues to play a track if one is paused.
     *
     * @param guild is the guild where the music should be continued.
     */
    public void continueTrack(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        musicManager.audioPlayer.setPaused(false);
    }

    /**
     * Sets the value of isRepeating
     *
     * @param guild  is the guild where the music should be repeated.
     * @param repeat is the new value.
     * @see TrackScheduler
     */
    public void setRepeat(Guild guild, boolean repeat) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        musicManager.trackScheduler.isRepeating = repeat;
    }

    /**
     * Returns weather the current track is being repeated or not.
     *
     * @param guild is the guild where the music is playing.
     * @return Boolean of the current repeating state.
     */
    public boolean isRepeating(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        return musicManager.trackScheduler.isRepeating;
    }

    /**
     * Clears the current track queue.
     *
     * @param guild is the guild where the music is playing.
     */
    public void clearQueue(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        musicManager.trackScheduler.queue.clear();
    }
}
