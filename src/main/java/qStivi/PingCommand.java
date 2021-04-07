package qStivi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements INewCommand {

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        JDA jda = event.getJDA();

        jda.getRestPing().queue(
                (RestActionPing) -> event.reply(
                        new EmbedBuilder()
                                .addField("Rest Action Ping: ", RestActionPing.toString(), false)
                                .addField("Gateway Ping: ", String.valueOf(jda.getGatewayPing()), false)
                                .build()
                ).queue()
        );

    }

    @Override
    public @NotNull String getName() {
        return "ping";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Displays bot delays.";
    }
}
