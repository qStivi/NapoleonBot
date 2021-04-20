package qStivi.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RollCommand implements ICommand {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        var rollInput = args[1];
        if (rollInput.equals("stats")) {
            hook.sendMessage(statsRoll()).queue();
        } else {
            var result = normalRoll(rollInput);
            if (result == null) {
                hook.sendMessage("This is not a valid roll!\nExample: `6d8`").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
                return;
            }
            hook.sendMessage(result).delay(Duration.ofMinutes(5)).flatMap(Message::delete).queue();
        }
    }


    MessageEmbed normalRoll(String query) {
        String[] input = query.split("d");
        int numOfDice;
        int numOfSides;
        try {
            numOfDice = Integer.parseInt(input[0]);
            numOfSides = Integer.parseInt(input[1]);
        } catch (Exception e) {
            return null;
        }

        List<Integer> rolls = new ArrayList<>();
        int sum = 0;

        for (int i = 0; i < numOfDice; i++) {
            int rand = ThreadLocalRandom.current().nextInt(1, numOfSides + 1);
            rolls.add(rand);
            sum += rand;
        }

        float mean = (float) sum / numOfDice;

        EmbedBuilder embed = new EmbedBuilder().setDescription("∑=" + sum + " | Ø=" + mean);

        if (rolls.stream().filter(integer -> integer == 1).count() > rolls.stream().filter(integer -> integer == numOfSides).count()) {
            embed.setColor(Color.red);
        } else if (rolls.stream().filter(integer -> integer == 1).count() < rolls.stream().filter(integer -> integer == numOfSides).count()) {
            embed.setColor(Color.green);
        } else if (rolls.containsAll(List.of(1, numOfSides)) && rolls.stream().filter(integer -> integer == 1).count() == rolls.stream().filter(integer -> integer == numOfSides).count()) {
            embed.setColor(Color.yellow);
        }

        if (numOfDice > 25) {
            embed.setFooter("Notice: Not all Dice could be displayed!");
        }

        for (int roll : rolls) {
            embed.addField("", String.valueOf(roll), true); // Embeds can hold a max. of 25 Fields so this will just stop after 25 Fields for now.
            if (embed.getFields().size() > 25) {
                break;
            }
        }

        return embed.build();
    }

    MessageEmbed statsRoll() {

        // 6*(2d6+6)

        List<Integer> rolls = new ArrayList<>();
        int sum = 0;

        for (int i = 0; i < 6; i++) {
            int rand = ThreadLocalRandom.current().nextInt(1, 7);
            int rand2 = ThreadLocalRandom.current().nextInt(1, 7);
            rand += rand2;
            rand += 6;
            rolls.add(rand);
            sum += rand;
        }

        EmbedBuilder embed = new EmbedBuilder().setDescription("∑=" + sum);

        for (int roll : rolls) {
            embed.addField("", String.valueOf(roll), true);
        }

        return embed.build();

    }

    @Override
    public @Nonnull
    String getName() {
        return "roll";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Clickety-Clackety, I roll to attackety";
    }
}
