package qStivi.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

public class PingCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        JDA jda = context.getJDA();

        jda.getRestPing().queue(
                (RestActionPing) -> context.getChannel().sendMessage(
                        new EmbedBuilder()
                                .addField("Rest Action Ping: ", RestActionPing.toString(), false)
                                .addField("Gateway Ping: ", String.valueOf(jda.getGatewayPing()), false)
                                .build()
                ).queue()
        );
    }

    @Override
    public String getHelp() {
        return "Shows ping between bot and Discord servers in milliseconds.";
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public List<String> getAliases() {
        return List.of("delay");
    }
}
