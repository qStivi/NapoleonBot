package qStivi.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.util.concurrent.atomic.AtomicReference;

public class Top10 implements ICommand {

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        //TODO highlight own name in list
        //TODO xp as tiebreaker
        var hook = event.getHook();
        var db = new DB();
        var embed = new EmbedBuilder();

        var list = db.getTop10();
        for (int i = 0; i < list.size(); i++) {
            Long id = list.get(i);
            var money = db.getMoney(id);
            var xp = db.getXp(id);

            AtomicReference<String> name = new AtomicReference<>();

            event.getJDA().retrieveUserById(id)
                    .map(User::getName)
                    .queue(name::set);

            while (name.get() == null) {
                Thread.onSpinWait();
            }
            embed.addField("", "#" + i + " [" + name.get() +"](https://youtu.be/dQw4w9WgXcQ) "+ money + " :gem: :white_small_square: " + xp + "xp", false);
        }
        hook.sendMessage(embed.build()).queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "top10";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Top 10 Players with the most money.";
    }
}
