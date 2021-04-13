package qStivi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;

import java.time.Duration;

public class LeaveCommand implements ICommand {

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
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
