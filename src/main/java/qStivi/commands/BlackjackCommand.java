package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        AtomicReference<String> messageId = new AtomicReference<>();
        hook.sendMessage("Loading...").queue(message -> messageId.set(message.getId()));
        while (messageId.get() == null) Thread.onSpinWait();
        var db = new DB();
        long id = event.getAuthor().getIdLong();
        if (db.userDoesNotExists(id)) {
            db.insert("users", "id", id);
        }
        var money = db.selectLong("users", "money", "id", id);
        if (money < Long.parseLong(args[1])) {
            hook.editMessageById(String.valueOf(messageId), "You don't have enough money!").delay(Duration.ofMinutes(5)).flatMap(Message::delete).queue();
            return;
        }
        db.increment("users", "command_times_blackjack", "id", id, 1);


        var removed = BlackJack.games.removeIf(game -> game.user.getIdLong() == id);
        if (removed) db.increment("users", "blackjack_loses", "id", id, 1);
        logger.info(messageId.get());
        BlackJack.games.add(new BlackJack(1, messageId.get(), event.getAuthor(), hook, Long.parseLong(args[1])));
        BlackJack bj = null;
        for (BlackJack game : BlackJack.games) {
            if (game.user.getIdLong() == id) {
                bj = game;
            }
        }

        db.decrement("users", "money", "id", id, bj.bet);

        displayGameState(bj);

        if (bj.count(bj.player) == 21) {
            event.getChannel().clearReactionsById(bj.id).queue();
            BlackJack.games.remove(bj);
            bj.embed.setTitle("You won!");
            db.increment("users", "money", "id", id, (long) Math.floor(bj.bet * 2.5));
            bj.embed.setColor(Color.green.brighter());
            db.increment("users", "blackjack_wins", "id", id, 1);
        } else {
            event.getChannel().addReactionById(bj.id, "\uD83E\uDD19\uD83C\uDFFD").queue();
            event.getChannel().addReactionById(bj.id, "✋\uD83C\uDFFD").queue();
        }

        hook.editMessageById(String.valueOf(messageId), bj.embed.build()).delay(Duration.ofMinutes(5)).flatMap(Message::delete).queue();
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
        if (title.equalsIgnoreCase("you won!")) {
            bj.embed.setColor(Color.green.brighter());
            db.increment("users", "blackjack_wins", "id", id, 1);
        }
        if (title.equalsIgnoreCase("you lost!")) {
            bj.embed.setColor(Color.red.brighter());
            db.increment("users", "blackjack_loses", "id", id, 1);
        }
        if (title.equalsIgnoreCase("draw.")) {
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
        bj.hook.editMessageById(bj.id, bj.embed.build()).queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "bj";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Don't count the cards!";
    }
}
