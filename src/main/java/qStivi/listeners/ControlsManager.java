package qStivi.listeners;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.audioManagers.PlayerManager;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static org.slf4j.LoggerFactory.getLogger;

public class ControlsManager extends ListenerAdapter {


    private static final Logger logger = getLogger(ControlsManager.class);

    private static ControlsManager INSTANCE;
    private final EmbedBuilder embed = new EmbedBuilder();
    TimerTask task;
    private Timer timer = new Timer();
    private String id;
    private String messageId;
    private long totalTime;
    private long timeRemaining;
    private String name;
    private String interpret;

    public static ControlsManager getINSTANCE() {

        if (INSTANCE == null) {
            INSTANCE = new ControlsManager();
        }

        return INSTANCE;
    }

    public void sendMessage(TextChannel channel, Guild guild) {
        deleteMessage(channel, guild);
        //noinspection StatementWithEmptyBody
        while (PlayerManager.getINSTANCE().getMusicManager(guild).audioPlayer.getPlayingTrack() == null) {
        } // wait for track to start playing
        this.id = PlayerManager.getINSTANCE().getMusicManager(guild).audioPlayer.getPlayingTrack().getIdentifier();
        channel.sendMessage("Loading...").queue(message -> this.messageId = message.getId());
        while (this.messageId == null) {
            Thread.onSpinWait();
        }

        channel.addReactionById(this.messageId, "▶").queue();
        channel.addReactionById(this.messageId, "⏸").queue();
        channel.addReactionById(this.messageId, "⏹").queue();
        channel.addReactionById(this.messageId, "\uD83D\uDD02").queue();
        channel.addReactionById(this.messageId, "⏭").queue();

        channel.editMessageById(messageId, "Currently playing...").queue();

        task = task(channel, guild);
        this.timer.schedule(task, 2000, 2000);
    }

    public void deleteMessage(TextChannel channel, Guild guild) {
        try {
            this.task.cancel();
            this.timer.cancel();
            this.timer = new Timer();
            this.task = task(channel, guild);
            channel.deleteMessageById(this.messageId).queue();
        } catch (Exception ignored) {
        }
        this.messageId = null;
    }

    private TimerTask task(TextChannel channel, Guild guild) {
        return new TimerTask() {

            @Override
            public void run() {
                Update(channel, guild);
                editMessage(channel, guild);
            }
        };
    }

    private void editMessage(TextChannel channel, Guild guild) {
        Random rand = new Random();
        final float hue = rand.nextFloat();
        // Saturation between 0.1 and 0.5
        final float saturation = (rand.nextInt(5000) + 1000) / 10000f;
        final float luminance = 0.9f;
        final Color color = Color.getHSBColor(hue, saturation, luminance);

        DateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeRemainingFormatted = formatter.format(this.timeRemaining);
        String totalTimeFormatted = formatter.format(this.totalTime);
        if (this.totalTime > 3600000) {
            totalTimeFormatted = "Too long bitch!";
        }


        this.embed.setColor(color)
                .setAuthor(interpret)
                .setTitle(name, "https://youtu.be/" + this.id)
                .setDescription(timeRemainingFormatted + "o-------------------------------------------------" + totalTimeFormatted);

        if (PlayerManager.getINSTANCE().isRepeating(guild)) {
            this.embed.setFooter("Currently repeating");
        } else {
            this.embed.setFooter(null);
        }

        if (messageId != null) channel.editMessageById(this.messageId, this.embed.build()).queue();
    }

    private void Update(TextChannel channel, Guild guild) {
        AudioTrack track = PlayerManager.getINSTANCE().getMusicManager(guild).audioPlayer.getPlayingTrack();
        if (track != null) {
            this.totalTime = track.getDuration();
            this.timeRemaining = track.getPosition();
            this.name = track.getInfo().title;
            this.id = PlayerManager.getINSTANCE().getMusicManager(guild).audioPlayer.getPlayingTrack().getIdentifier();
            this.interpret = track.getInfo().author;
        } else {
            deleteMessage(channel, guild);
        }
    }

    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
        if (event.getUser() == null) return;
        if (event.retrieveMessage().complete().getContentRaw().contains("Currently playing..."))
            if (!event.getUser().isBot()) {

                if (event.getReactionEmote().getEmoji().equals("⏸")) {
                    PlayerManager.getINSTANCE().pause(event.getGuild());
                }

                if (event.getReactionEmote().getEmoji().equals("▶")) {
                    PlayerManager.getINSTANCE().continueTrack(event.getGuild());
                }

                if (event.getReactionEmote().getEmoji().equals("⏹")) {
                    PlayerManager.getINSTANCE().clearQueue(event.getGuild());
                    PlayerManager.getINSTANCE().skip(event.getGuild());
                }

                if (event.getReactionEmote().getEmoji().equals("\uD83D\uDD02")) {
                    PlayerManager.getINSTANCE().setRepeat(event.getGuild(), !PlayerManager.getINSTANCE().isRepeating(event.getGuild()));
                }

                if (event.getReactionEmote().getEmoji().equals("⏭")) {
                    PlayerManager.getINSTANCE().skip(event.getGuild());
                }
            }

    }
}
