package qStivi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlackJack {
    public String id;
    public EmbedBuilder embed = new EmbedBuilder();
    public CommandHook hook;
    public long bet;
    public final List<Card> dealer = new ArrayList<>();
    public final List<Card> player = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    public User user;
    public static List<BlackJack> games = new ArrayList<>();

    public BlackJack(int numberOfDecks, String id, User user, CommandHook hook, long bet) {
        for (int i = 0; i < numberOfDecks; i++) {
            cards.add(new Card(Suit.Clubs, 0, Emotes.ACE_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 2, Emotes.TWO_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 3, Emotes.THREE_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 4, Emotes.FOUR_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 5, Emotes.FIVE_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 6, Emotes.SIX_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 7, Emotes.SEVEN_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 8, Emotes.EIGHT_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 9, Emotes.NINE_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 10, Emotes.TEN_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 10, Emotes.JESTER_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 10, Emotes.QUEEN_OF_CLUBS));
            cards.add(new Card(Suit.Clubs, 10, Emotes.KING_OF_CLUBS));

            cards.add(new Card(Suit.Hearts, 0, Emotes.ACE_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 2, Emotes.TWO_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 3, Emotes.THREE_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 4, Emotes.FOUR_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 5, Emotes.FIVE_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 6, Emotes.SIX_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 7, Emotes.SEVEN_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 8, Emotes.EIGHT_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 9, Emotes.NINE_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 10, Emotes.TEN_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 10, Emotes.JESTER_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 10, Emotes.QUEEN_OF_HEARTS));
            cards.add(new Card(Suit.Hearts, 10, Emotes.KING_OF_HEARTS));

            cards.add(new Card(Suit.Diamonds, 0, Emotes.ACE_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 2, Emotes.TWO_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 3, Emotes.THREE_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 4, Emotes.FOUR_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 5, Emotes.FIVE_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 6, Emotes.SIX_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 7, Emotes.SEVEN_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 8, Emotes.EIGHT_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 9, Emotes.NINE_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 10, Emotes.TEN_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 10, Emotes.JESTER_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 10, Emotes.QUEEN_OF_DIAMONDS));
            cards.add(new Card(Suit.Diamonds, 10, Emotes.KING_OF_DIAMONDS));

            cards.add(new Card(Suit.Spades, 0, Emotes.ACE_OF_SPADES));
            cards.add(new Card(Suit.Spades, 2, Emotes.TWO_OF_SPADES));
            cards.add(new Card(Suit.Spades, 3, Emotes.THREE_OF_SPADES));
            cards.add(new Card(Suit.Spades, 4, Emotes.FOUR_OF_SPADES));
            cards.add(new Card(Suit.Spades, 5, Emotes.FIVE_OF_SPADES));
            cards.add(new Card(Suit.Spades, 6, Emotes.SIX_OF_SPADES));
            cards.add(new Card(Suit.Spades, 7, Emotes.SEVEN_OF_SPADES));
            cards.add(new Card(Suit.Spades, 8, Emotes.EIGHT_OF_SPADES));
            cards.add(new Card(Suit.Spades, 9, Emotes.NINE_OF_SPADES));
            cards.add(new Card(Suit.Spades, 10, Emotes.TEN_OF_SPADES));
            cards.add(new Card(Suit.Spades, 10, Emotes.JESTER_OF_SPADES));
            cards.add(new Card(Suit.Spades, 10, Emotes.QUEEN_OF_SPADES));
            cards.add(new Card(Suit.Spades, 10, Emotes.KING_OF_SPADES));
        }
        Collections.shuffle(cards);
        this.bet = bet;
        this.hook = hook;
        this.user = user;
        this.id = id;

        dealer.add(draw());

        player.add(draw());
        player.add(draw());
    }

    public int hit() {
        player.add(draw());
        return count(player);
    }

    public int stand() {

        while (count(dealer) <= 17){
            dealer.add(draw());
        }

        return count(dealer);
    }

    public int count(List<Card> hand) {
        int value = 0;
        for (Card card : hand) {
            if (card.value != 0) {
                value += card.value;
            } else if (value <= 10) {
                value += 11;
            } else {
                value++;
            }
        }
        return value;
    }

    /***
     * Returns a random {@link Card} from the "stack" of the instance or null if the "stack" is empty.
     *
     * @return {@link Card}
     */
    @Nullable
    @CheckForNull
    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
//        var r = ThreadLocalRandom.current().nextInt(cards.size());

        var card = cards.get(0);
        cards.remove(card);
        return card;
    }
}
