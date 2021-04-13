package qStivi.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.slf4j.Logger;
import qStivi.ICommand;

import javax.annotation.Nonnull;

import static org.slf4j.LoggerFactory.getLogger;

public class ShutdownCommand implements ICommand {
    private static final Logger logger = getLogger(ShutdownCommand.class);

    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        logger.info("Shutting down...");

        event.getJDA().shutdownNow();

        System.exit(0);
    }

    @Override
    public @Nonnull
    String getName() {
        return "shutdown";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Shuts down the bot.";
    }
}
