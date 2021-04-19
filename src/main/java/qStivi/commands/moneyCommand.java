package qStivi.commands;

import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;

public class moneyCommand implements ICommand {
    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addSubcommand(new CommandUpdateAction.SubcommandData("give", "Gives Money")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.USER, "user", "user to give money to")
                                .setRequired(true))
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "amount", "amount of money to give")
                                .setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("remove", "Gives Money")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.USER, "user", "user to remove money from")
                                .setRequired(true))
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "amount", "amount of money to give")
                                .setRequired(true)));
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        if (!(event.getUser().getIdLong() == 219108246143631364L)){
            hook.sendMessage("You don't have the permission to do that").delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
            return;
        }

        var subcommand = event.getSubcommandName();
        var userID = event.getOption("user").getAsUser().getIdLong();
        var amount = event.getOption("amount").getAsLong();

        var db = new DB();
        if (subcommand.equals("give")){
            db.increment("users", "money", "id", userID, amount);
        }
        if (subcommand.equals("remove")){
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
