package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import qStivi.ICommand;
import qStivi.audioManagers.PlayerManager;

import javax.annotation.Nonnull;
import java.time.Duration;

public class PauseCommand implements ICommand {

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        PlayerManager.getINSTANCE().pause(event.getGuild());
        hook.sendMessage("Playback paused.").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
    }

    @Override
    public @Nonnull
    String getName() {
        return "pause";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Pauses music playback.";
    }
}
