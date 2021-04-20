package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;

import java.time.Duration;

public class LeaveCommand implements ICommand {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        event.getGuild().getAudioManager();
        event.getGuild().getAudioManager().closeAudioConnection();
        hook.sendMessage("Bye Bye").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
    }

    @Override
    public @NotNull String getName() {
        return "leave";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Disconnects bot from any voice channel.";
    }
}
