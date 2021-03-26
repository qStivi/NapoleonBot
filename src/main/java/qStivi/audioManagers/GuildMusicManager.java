package qStivi.audioManagers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildMusicManager {

    public final AudioPlayer audioPlayer;

    public final TrackScheduler trackScheduler;

    private final AudioSendHandler audioSendHandler;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.trackScheduler);
        this.audioSendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public AudioSendHandler getAudioSendHandler() {
        return audioSendHandler;
    }
}
