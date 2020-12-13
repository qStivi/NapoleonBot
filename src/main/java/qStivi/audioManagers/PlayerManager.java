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

    /**
     * This variable exists so we can use the same instance in the whole program. This is important because we always want to use the same queue for example.
     */
    private static PlayerManager INSTANCE;

    public GuildMusicManager gm;


    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager(Guild guild) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.gm = new GuildMusicManager(this.audioPlayerManager);

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

        guild.getAudioManager().setSendingHandler(gm.getAudioSendHandler());
    }

    public static PlayerManager getINSTANCE(Guild guild) {

        if (INSTANCE == null) {
            INSTANCE = new PlayerManager(guild);
        }

        return INSTANCE;
    }

    public void loadAndPlay(TextChannel channel, String trackURL) {
        final GuildMusicManager musicManager = this.gm;

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
    public void skip() {
        final GuildMusicManager musicManager = this.gm;

        musicManager.audioPlayer.startTrack(musicManager.trackScheduler.queue.poll(), false);
    }

    public void pause() {
        final GuildMusicManager musicManager = this.gm;

        musicManager.audioPlayer.setPaused(true);
    }

    public void continueTrack() {
        final GuildMusicManager musicManager = this.gm;

        musicManager.audioPlayer.setPaused(false);
    }

    public void setRepeat(boolean repeat) {
        final GuildMusicManager musicManager = this.gm;

        musicManager.trackScheduler.isRepeating = repeat;
    }

    public boolean isRepeating() {
        final GuildMusicManager musicManager = this.gm;

        return musicManager.trackScheduler.isRepeating;
    }

    public void clearQueue() {
        final GuildMusicManager musicManager = this.gm;

        musicManager.trackScheduler.queue.clear();
    }
}
