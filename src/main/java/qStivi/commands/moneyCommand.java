package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;

public class moneyCommand implements ICommand {

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        if (!(event.getAuthor().getIdLong() == 219108246143631364L)) {
            hook.sendMessage("You don't have the permission to do that").delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
            return;
        }

        var subcommand = args[1];
        var userID = event.getMessage().getMentionedUsers().get(0).getIdLong();
        var amount = Long.parseLong(args[3]);

        var db = new DB();
        if (subcommand.equals("give")) {
            db.increment("users", "money", "id", userID, amount);
        }
        if (subcommand.equals("remove")) {
            db.decrement("users", "money", "id", userID, amount);
        }
        hook.sendMessage("Done!").delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "money";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Manages money.";
    }
}
