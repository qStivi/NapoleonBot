package qStivi.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

public class Top10Command implements ICommand {

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

        var list = db.getRanking();
        var size = Math.min(list.size(), 10);
        for (int i = 0; i < size; i++) {
            Long id = list.get(i);
            var money = db.selectLong("users", "money", "id", id);
            var xp = db.selectLong("users", "xp", "id", id);
            xp = xp==null?0:xp;
            var lvl = (int) Math.floor((double) xp / 800);

            AtomicReference<String> name = new AtomicReference<>();

            event.getJDA().retrieveUserById(id)
                    .map(User::getName)
                    .queue(name::set);

            while (name.get() == null) {
                Thread.onSpinWait();
            }
            embed.addField("", "#" + i + " [" + name.get() + "](https://youtu.be/dQw4w9WgXcQ) " + money + " :gem: :white_small_square: " + xp + "xp LVL: " + lvl, false);
        }
        var userIDs = db.getRanking();
        double winLoseRatio = 0;
        for (Long id : userIDs) {
            var wins = db.selectLong("users", "blackjack_wins", "id", id);
            var loses = db.selectLong("users", "blackjack_loses", "id", id);
            wins=wins==null?0:wins;
            loses=loses==null?0:loses;
            winLoseRatio = (double) wins / loses;
        }
        embed.setFooter("Average BlackJack win/lose ratio: " + winLoseRatio);
        hook.sendMessage(embed.build()).delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
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
