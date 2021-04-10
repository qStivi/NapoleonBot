package qStivi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.ICommand;
import qStivi.audioManagers.PlayerManager;

import javax.annotation.Nonnull;
import java.time.Duration;

public class PauseCommand implements ICommand {
    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        PlayerManager.getINSTANCE().pause(event.getGuild());
        event.reply("Playback paused.").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();
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
