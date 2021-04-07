package qStivi;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class LeaveCommand implements INewCommand {

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        event.getGuild().getAudioManager();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply("Bye Bye").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();
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
