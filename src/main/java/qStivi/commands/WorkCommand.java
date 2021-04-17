package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;
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

        if (!db.userExists(id)) {
            db.insert(id);
        }

        var seconds = db.getLastWorked(id);
        var millis = seconds * 1000;
        var lastWorked = new Date(millis);
        var now = new Date();
        var diff = (now.getTime() - lastWorked.getTime()) / 1000;

        if (diff > 3600){
            db.updateMoney(id, db.getMoney(id) + 100);
            hook.sendMessage("You earned 100 gems").delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
        }else {
            hook.sendMessage("You need to wait " + Math.subtractExact(3600L, diff) + " seconds before you can work again").delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
        }

        db.updateLastWorked(id, now.getTime() / 1000);
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
