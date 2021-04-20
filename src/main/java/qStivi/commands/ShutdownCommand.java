package qStivi.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import qStivi.ICommand;

import javax.annotation.Nonnull;

import static org.slf4j.LoggerFactory.getLogger;

public class ShutdownCommand implements ICommand {
    private static final Logger logger = getLogger(ShutdownCommand.class);

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
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
