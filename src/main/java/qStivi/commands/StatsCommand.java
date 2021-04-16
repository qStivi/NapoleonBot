package qStivi.commands;

import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;

public class StatsCommand implements ICommand {
    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.USER, "user", "@username")
                        .setRequired(false));
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        var db = new DB();
        var user = event.getOption("user");

        long id = user==null?Long.parseLong(event.getUser().getId()):Long.parseLong(user.getAsUser().getId());

        var money = db.getMoney(id);
        var xp = db.getXp(id);
        var lvl = (int) Math.floor(xp / 800);

        hook.sendMessage("Level: " + lvl + "\nMoney: " + money + "\nXP: " + xp).delay(Duration.ofHours(1)).flatMap(Message::delete).queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "stats";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "See cool stuff.";
    }
}
