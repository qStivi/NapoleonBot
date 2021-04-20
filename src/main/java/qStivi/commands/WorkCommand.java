package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;
import java.util.Date;

public class WorkCommand implements ICommand {

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        var id = event.getAuthor().getIdLong();
        var db = new DB();

        if (db.userDoesNotExists(id)) {
            db.insert("users", "id", id);
        }

        var seconds = db.selectLong("users", "last_worked", "id", id);
        seconds = seconds == null ? 0 : seconds;
        var millis = seconds * 1000;
        var lastWorked = new Date(millis);
        var now = new Date();
        var diff = (now.getTime() - lastWorked.getTime()) / 1000;
        var xp = db.selectLong("users", "xp", "id", id);
        xp = xp == null ? 0 : xp;
        var lvl = (long) Math.floor(xp / (double) 800);
        long lone = 1000 + (lvl * 10);

        if (diff > 1200) {
            db.increment("users", "money", "id", id, lone);
            hook.sendMessage("You earned " + lone + " gems").delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
            db.update("users", "last_worked", "id", id, now.getTime() / 1000);
        } else {
            hook.sendMessage("You need to wait " + Math.subtractExact(1200L, diff) + " seconds before you can work again").delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
        }

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
