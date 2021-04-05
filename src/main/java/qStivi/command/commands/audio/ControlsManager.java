package qStivi.command.commands.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ControlsManager extends ListenerAdapter {
    private static ControlsManager INSTANCE;
    private final EmbedBuilder embed = new EmbedBuilder();
    private Timer timer = new Timer();
    private String id;
    private String messageId;
    private long totalTime;
    private long timeRemaining;
    private String name;
    private String interpret;
    private CommandContext context;
    private TimerTask task = task();

    public static ControlsManager getINSTANCE() {

        if (INSTANCE == null) {
            INSTANCE = new ControlsManager();
        }

        return INSTANCE;
    }

    public void sendMessage(CommandContext context) {
        //noinspection StatementWithEmptyBody
        while (PlayerManager.getINSTANCE().getMusicManager(context.getGuild()).audioPlayer.getPlayingTrack() == null) {
        } // wait for track to start playing
        this.context = context;
        this.id = PlayerManager.getINSTANCE().getMusicManager(context.getGuild()).audioPlayer.getPlayingTrack().getIdentifier();
        context.getChannel().sendMessage("Loading...").queue(message -> this.messageId = message.getId());
        while (this.messageId == null) {
            Thread.onSpinWait();
        }

        context.getChannel().addReactionById(this.messageId, "▶").queue();
        context.getChannel().addReactionById(this.messageId, "⏸").queue();
        context.getChannel().addReactionById(this.messageId, "⏹").queue();
        context.getChannel().addReactionById(this.messageId, "\uD83D\uDD02").queue();
        context.getChannel().addReactionById(this.messageId, "⏭").queue();

        context.getChannel().editMessageById(messageId, "Currently playing...").queue();

        this.timer.schedule(task, 2000, 2000);
    }

    public void deleteMessage() {
        this.task.cancel();
        this.timer.cancel();
        this.timer = new Timer();
        this.task = task();
        this.context.getChannel().deleteMessageById(this.messageId).queue();
        this.messageId = null;
    }

    private TimerTask task() {
        return new TimerTask() {

            @Override
            public void run() {
                Update();
                editMessage();
            }
        };
    }

    private void editMessage() {
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

        if (PlayerManager.getINSTANCE().isRepeating(this.context.getGuild())) {
            this.embed.setFooter("Currently repeating");
        } else {
            this.embed.setFooter(null);
        }

        if (messageId != null) this.context.getChannel().editMessageById(this.messageId, this.embed.build()).queue();
    }

    private void Update() {
        AudioTrack track = PlayerManager.getINSTANCE().getMusicManager(this.context.getGuild()).audioPlayer.getPlayingTrack();
        if (track != null) {
            this.totalTime = track.getDuration();
            this.timeRemaining = track.getPosition();
            this.name = track.getInfo().title;
            this.interpret = track.getInfo().author;
        } else {
            deleteMessage();
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
