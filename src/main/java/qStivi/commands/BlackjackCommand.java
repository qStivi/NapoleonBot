package qStivi.commands;

import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.BlackJack;
import qStivi.Card;
import qStivi.ICommand;
import qStivi.db.DB;

import java.awt.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("ConstantConditions")
public class BlackjackCommand extends ListenerAdapter implements ICommand {
    private static final Logger logger = getLogger(BlackjackCommand.class);

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "bet", "How much do you want to bet?")
                        .setRequired(true));
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        AtomicReference<String> messageId = new AtomicReference<>();
        hook.sendMessage("Loading...").queue(message -> messageId.set(message.getId()));
        while (messageId.get() == null) Thread.onSpinWait();
        var db = new DB();
        long id = event.getUser().getIdLong();
        if (db.userDoesNotExists(id)) {
            db.insert("users", "id", id);
        }
        var money = db.selectLong("users", "money", "id", id);
        if (money < event.getOption("bet").getAsLong()) {
            hook.editOriginal("You don't have enough money!").delay(Duration.ofMinutes(5)).flatMap(Message::delete).queue();
            return;
        }
        db.increment("users", "command_times_blackjack", "id", event.getUser().getIdLong(), 1);


        var removed = BlackJack.games.removeIf(game -> game.user.getId().equals(event.getUser().getId()));
        if (removed) db.increment("users", "blackjack_loses", "id", id, 1);
        BlackJack.games.add(new BlackJack(1, messageId.get(), event.getUser(), hook, event.getOption("bet").getAsLong()));
        BlackJack bj = null;
        for (BlackJack game : BlackJack.games) {
            if (game.user.getId().equals(event.getUser().getId())) {
                bj = game;
            }
        }

        db.decrement("users", "money", "id", id, bj.bet);

        displayGameState(bj);

        if (bj.count(bj.player) == 21) {
            event.getTextChannel().clearReactionsById(bj.id).queue();
            BlackJack.games.remove(bj);
            bj.embed.setTitle("You won!");
            db.increment("users", "money", "id", id, (long) Math.floor(bj.bet * 2.5));
            bj.embed.setColor(Color.green.brighter());
            db.increment("users", "blackjack_wins", "id", id, 1);
        } else {
            event.getChannel().addReactionById(bj.id, "\uD83E\uDD19\uD83C\uDFFD").queue();
            event.getChannel().addReactionById(bj.id, "✋\uD83C\uDFFD").queue();
        }

        hook.editOriginal(bj.embed.build()).delay(Duration.ofMinutes(5)).flatMap(Message::delete).queue();
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        var user = event.getUser();
        var db = new DB();

        BlackJack bj = null;
        for (BlackJack game : BlackJack.games) {
            if (game.user.getId().equals(event.getUser().getId())) {
                bj = game;
            }
        }

        if (bj != null) {
            String messageId = event.getMessageId();
            if (messageId.equals(bj.id) && !Objects.requireNonNull(event.getUser()).isBot()) {
                if (event.getReactionEmote().getEmoji().equals("\uD83E\uDD19\uD83C\uDFFD")) {
                    event.getReaction().removeReaction(user).queue();
                    if (bj.hit() > 21) {
                        endGame(event, db, bj, 0, "You Lost!");
                    }
                }
                if (event.getReactionEmote().getEmoji().equals("✋\uD83C\uDFFD")) {
                    event.getReaction().removeReaction(user).queue();
                    var dealerHandValue = bj.stand();
                    var playerHandValue = bj.count(bj.player);

                    if (dealerHandValue > 21 && playerHandValue <= 21) endGame(event, db, bj, bj.bet * 2, "You won!");
                    else if (dealerHandValue < playerHandValue) endGame(event, db, bj, bj.bet * 2, "You won!");

                    else if (playerHandValue > 21 && dealerHandValue <= 21) endGame(event, db, bj, 0, "You Lost!");
                    else if (dealerHandValue > playerHandValue) endGame(event, db, bj, 0, "You Lost!");

                    else endGame(event, db, bj, bj.bet, "Draw.");
                }
                displayGameState(bj);
            }
        }
    }

    private void endGame(@NotNull GuildMessageReactionAddEvent event, DB db, BlackJack bj, long reward, String title) {
        var id = event.getUser().getIdLong();
        var messageId = event.getMessageId();
        bj.embed.setTitle(title);
        db.increment("users", "money", "id", id, reward);
        event.getChannel().clearReactionsById(messageId).queue();
        BlackJack.games.remove(bj);
        if (title.equalsIgnoreCase("you won!")){
            bj.embed.setColor(Color.green.brighter());
            db.increment("users", "blackjack_wins", "id", id, 1);
        }
        if (title.equalsIgnoreCase("you lost!")){
            bj.embed.setColor(Color.red.brighter());
            db.increment("users", "blackjack_loses", "id", id, 1);
        }
        if (title.equalsIgnoreCase("draw.")){
            bj.embed.setColor(Color.magenta.darker());
            db.increment("users", "blackjack_draws", "id", id, 1);
        }
    }

    private void displayGameState(BlackJack bj) {
        bj.embed.clearFields();
        bj.embed.addField("Dealer", String.valueOf(bj.count(bj.dealer)), true);

        StringBuilder dealerCards = new StringBuilder();
        var dealer = bj.dealer;
        for (Card card : dealer) {
            dealerCards.append("<:").append(card.emote).append("> ");
        }

        bj.embed.addField("", dealerCards.toString(), true);


        bj.embed.addBlankField(false);


        bj.embed.addField("Player", String.valueOf(bj.count(bj.player)), true);

        StringBuilder playerCards = new StringBuilder();
        var player = bj.player;
        for (Card card : player) {
            playerCards.append("<:").append(card.emote).append("> ");
        }

        bj.embed.addField("", playerCards.toString(), true);
        bj.hook.editOriginal(bj.embed.build()).queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "blackjack";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Don't count the cards!";
    }
}
