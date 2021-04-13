package qStivi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.audioManagers.PlayerManager;

import javax.annotation.Nonnull;
import java.time.Duration;

public class SkipCommand implements ICommand {

    @Override
    @Nonnull
    public CommandUpdateAction.@NotNull CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        PlayerManager.getINSTANCE().skip(event.getGuild());
        hook.sendMessage("Skipping...").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
    }

    @Override
    public @Nonnull
    String getName() {
        return "skip";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Plays next song in queue.";
    }
}
