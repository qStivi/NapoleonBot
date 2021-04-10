package qStivi;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import javax.annotation.Nonnull;

public interface ICommand {
    @Nonnull
    CommandUpdateAction.CommandData getCommand();

    void handle(SlashCommandEvent event);

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();
}
