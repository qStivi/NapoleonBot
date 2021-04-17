package qStivi.commands;

import net.dv8tion.jda.api.entities.Command;
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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.slf4j.LoggerFactory.getLogger;

public class BlackjackCommand extends ListenerAdapter implements ICommand {
    private static final Logger logger = getLogger(BlackjackCommand.class);

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "stake", "How much do you want to bet?")
                        .setRequired(true));
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        AtomicReference<String> id = new AtomicReference<>();
        hook.sendMessage("Loading...").queue(message -> {
            id.set(message.getId());
        });
        while (id.get() == null) Thread.onSpinWait();

        var db = new DB();
        var money = db.getMoney(Long.parseLong(event.getUser().getId()));
        if (money < event.getOption("stake").getAsLong()) {
            hook.sendMessage("You don't have enough money!").queue();
            return;
        }

        BlackJack.games.add(new BlackJack(1, id.get(), event.getUser(), hook, event.getOption("stake").getAsLong()));

        BlackJack bj = null;
        for (BlackJack game : BlackJack.games) {
            if (game.user.getId().equals(event.getUser().getId())) {
                bj = game;
            }
        }

        bj.embed.addField("Dealer", String.valueOf(bj.count(bj.dealer)), true);
        bj.embed.addField("Player", String.valueOf(bj.count(bj.player)), true);

        hook.editOriginal(bj.embed.build()).queue();

        event.getChannel().addReactionById(bj.id, "\uD83E\uDD19\uD83C\uDFFD").queue();
        event.getChannel().addReactionById(bj.id, "✋\uD83C\uDFFD").queue();
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

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
                    var value = bj.hit();
                    logger.info("hit");
                    event.getReaction().removeReaction(event.getUser()).queue();
                    logger.info(String.valueOf(value));
                    if (value > 21) {
                        bj.embed.setTitle("You lost!");
                        var db = new DB();
                        var money = db.getMoney(Long.parseLong(bj.user.getId()));
                        db.updateMoney(Long.parseLong(bj.user.getId()), money - bj.stake);
                        event.getChannel().clearReactionsById(messageId).queue();
                    }
                }
                if (event.getReactionEmote().getEmoji().equals("✋\uD83C\uDFFD")) {
                    var value = bj.stand();
                    var pvalue = bj.count(bj.player);
                    logger.info("stand");
                    event.getReaction().removeReaction(event.getUser()).queue();
                    logger.info(String.valueOf(value));
                    logger.info(String.valueOf(pvalue));
                    if (value >= 21) {
                        bj.embed.setTitle("You won!");
                        var db = new DB();
                        var money = db.getMoney(Long.parseLong(bj.user.getId()));
                        db.updateMoney(Long.parseLong(bj.user.getId()), money + bj.stake);
                        event.getChannel().clearReactionsById(messageId).queue();
                    } else {
                        if (value > pvalue) {
                            bj.embed.setTitle("You lost!");
                            var db = new DB();
                            var money = db.getMoney(Long.parseLong(bj.user.getId()));
                            db.updateMoney(Long.parseLong(bj.user.getId()), money - bj.stake);
                            event.getChannel().clearReactionsById(messageId).queue();
                        } else if (value == pvalue) {
                            bj.embed.setTitle("Draw.");
                            event.getChannel().clearReactionsById(messageId).queue();
                        } else {
                            bj.embed.setTitle("You won!");
                            var db = new DB();
                            var money = db.getMoney(Long.parseLong(bj.user.getId()));
                            db.updateMoney(Long.parseLong(bj.user.getId()), money + bj.stake);
                            event.getChannel().clearReactionsById(messageId).queue();
                        }
                    }
                }
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
        }
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
