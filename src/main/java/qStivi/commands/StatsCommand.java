package qStivi.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.db.DB;

import java.time.Duration;

public class StatsCommand implements ICommand {

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        var db = new DB();
        var commandUser = event.getMessage().getMentionedMembers().size()>0?event.getMessage().getMentionedMembers().get(0):null;

        var user = commandUser == null ? event.getMember() : commandUser;
        var userID = user.getIdLong();

        if (db.userDoesNotExists(userID)) {
            db.insert("users", "id", user);
        }

        var money = db.selectLong("users", "money", "id", userID);
        var xp = db.selectLong("users", "xp", "id", userID);
        var lvl = (long) Math.floor(xp / 800);
        var userName = user.getEffectiveName();
        var ranking = db.getRanking();
        long position = 1337;
        long blackJackWins = db.selectLong("users", "blackjack_wins", "id", userID);
        long blackJackLoses = db.selectLong("users", "blackjack_loses", "id", userID);
        if (blackJackLoses == 0) blackJackLoses = 1;
        var winLoseRatio = (double) blackJackWins / blackJackLoses;

        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i) == event.getAuthor().getIdLong()) {
                position = i;
            }
        }

        var embed = new EmbedBuilder();
        embed.setColor(user.getColor());
        embed.setAuthor(userName, "https://youtu.be/dQw4w9WgXcQ", user.getUser().getEffectiveAvatarUrl());
        if (position != 1337) embed.addField("Rank", "#" + position, false);
        embed.addField("Level", String.valueOf(lvl), true);
        embed.addField("Money", money + " :gem:", true);
        embed.addField("XP", String.valueOf(xp), true);
        embed.setFooter("BlackJack win/lose ratio: " + winLoseRatio);

        hook.sendMessage(embed.build()).delay(Duration.ofMinutes(1)).flatMap(Message::delete).queue();
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
