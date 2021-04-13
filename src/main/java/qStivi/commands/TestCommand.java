package qStivi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.time.Duration;

public class TestCommand implements ICommand {

    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        event.getHook().sendMessage("k").delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
    }

    @Override
    public @Nonnull
    String getName() {
        return "test";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "This is a test command";
    }
}
