package qStivi.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RollCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        List<String> args = context.getArgs();

        if (args.get(0).equalsIgnoreCase("stats")) {

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

            context.getChannel().sendMessage(embed.build()).queue();



        } else {
            String[] input = args.get(0).split("d");
            int numOfDice = Integer.parseInt(input[0]);
            int numOfSides = Integer.parseInt(input[1]);

            List<Integer> rolls = new ArrayList<>();
            int sum = 0;

            for (int i = 0; i < numOfDice; i++) {
                int rand = ThreadLocalRandom.current().nextInt(1, numOfSides + 1);
                rolls.add(rand);
                sum += rand;
            }

            float mean = (float) sum/numOfDice;

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

            context.getChannel().sendMessage(embed.build()).queue();
        }
    }

    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public String getHelp() {
        return "Rolls dice for you. `/r 6d4` `/r stats`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r", "rol", "dice", "d", "w", "würfel");
    }
}
