package qStivi.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.ICommand;
import qStivi.db.DB;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class WorkCommand implements ICommand {
    private static final Logger logger = getLogger(DB.class);

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        var id = Long.parseLong(event.getUser().getId());
        var db = new DB();

        var seconds = db.getLastWorked(id);
        var millis = seconds * 1000;
        var lastWorked = new Date(millis);
        var now = new Date();
        var diff = (now.getTime() - lastWorked.getTime()) / 1000;
        hook.sendMessage(String.valueOf(diff)).queue();

        db.updateLastWorked(id, now.getTime() / 1000);

        logger.info(lastWorked.toString());
        logger.info(now.toString());
        logger.info(String.valueOf(diff));

    }

    @NotNull
    @Override
    public String getName() {
        return "work";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get money by working.";
    }
}
