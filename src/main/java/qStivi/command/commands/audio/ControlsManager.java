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
import java.util.*;

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
        if (this.messageId == null) {
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

            context.getChannel().pinMessageById(this.messageId).queue();

            this.timer.schedule(task, 2000, 2000);
        } else {
            context.getChannel().sendMessage("The Controller is Pinned.").queue();
        }
    }

    public void deleteMessage() {
        this.task.cancel();
        this.timer.cancel();
        try {
            this.context.getChannel().unpinMessageById(this.messageId).queue();
        } catch (Exception ignored) {
        }
        this.timer = new Timer();
        this.task = task();
        try {
            this.context.getChannel().deleteMessageById(this.messageId).queue();
        } catch (Exception ignored) {
        }
        this.messageId = null;
    }

    private TimerTask task() {
        return new TimerTask() {
            final long time = System.currentTimeMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() - time > 1800 * 1000) {
                    deleteMessage();
                } else {
                    Update();
                    editMessage();
                }
            }
        };
    }

    private void editMessage() {
        Random rand = new Random();
        final float hue = rand.nextFloat();
        // Saturation between 0.1 and 0.3
        final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        final Color color = Color.getHSBColor(hue, saturation, luminance);

        DateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeRemainingFormatted = formatter.format(this.timeRemaining);
        String totalTimeFormatted = formatter.format(this.totalTime);


        this.embed.setColor(color)
                .setAuthor(interpret)
                .setTitle(name, "https://youtu.be/" + this.id)
                .setDescription(timeRemainingFormatted + "o-------------------------------------------------" + totalTimeFormatted);

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
        if (!Objects.requireNonNull(event.getUser()).isBot()) {

            if (event.getReactionEmote().getEmoji().equals("⏸")) {
                PlayerManager.getINSTANCE().pause(event.getGuild());
            }

            if (event.getReactionEmote().getEmoji().equals("▶")) {
                PlayerManager.getINSTANCE().continueTrack(event.getGuild());
            }

            if (event.getReactionEmote().getEmoji().equals("⏹")) {
                PlayerManager.getINSTANCE().clearQueue(event.getGuild());
            }

            if (event.getReactionEmote().getEmoji().equals("\uD83D\uDD02")) {
                PlayerManager.getINSTANCE().setRepeat(event.getGuild(), !PlayerManager.getINSTANCE().isRepeating(event.getGuild()));
            }

            if (event.getReactionEmote().getEmoji().equals("⏭")) {
                PlayerManager.getINSTANCE().skip(event.getGuild());
            }
        }

    }

//    @Override
//    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
////        if (event.getMessageId().equals(this.messageId)){
////            event.getChannel().sendMessage(event.getMessageId() + " | " + messageId).queue();
////            deleteMessage();
////        }
//    }
}
