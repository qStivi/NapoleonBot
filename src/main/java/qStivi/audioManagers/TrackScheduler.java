package qStivi.audioManagers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    /**
     * The list we use as our queue.
     *
     * @see BlockingQueue
     */
    final BlockingQueue<AudioTrack> queue;
    /**
     * TODO
     */
    private final AudioPlayer player;
    /**
     * This variable is used to keep track of weather the current {@link AudioTrack} should be repeated or not.
     *
     * @see TrackScheduler
     */
    boolean isRepeating;

    /**
     * This constructor is used to set the {@link AudioPlayer} and initialize the queue.
     *
     * @param player Discord's {@link AudioPlayer}
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Handles what happens after a track has stopped playing.
     *
     * @param player    Discord's {@link AudioPlayer}
     * @param track     {@link AudioTrack} which has stopped playing.
     * @param endReason The reason why the {@link AudioTrack} has stopped playing.
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (!isRepeating) {
                this.player.startTrack(this.queue.poll(), false);
            } else {
                this.player.startTrack(track.makeClone(), false);
            }
        }
    }

    /**
     * Starts playing a {@link AudioTrack} or adds it to the queue if it couldn't be played without interrupting another.
     *
     * @param track {@link AudioTrack} to be queued.
     */
    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }
}
