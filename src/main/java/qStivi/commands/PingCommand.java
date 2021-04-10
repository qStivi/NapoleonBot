package qStivi.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;

import java.text.MessageFormat;
import java.time.Duration;

public class PingCommand implements ICommand {

    private volatile Long ping;

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        long gatewayPing = event.getJDA().getGatewayPing();
        event.getJDA().getRestPing().queue((ping) -> {
            event.getTextChannel().sendMessage("Pinging...").flatMap(Message::delete).queue();
            this.ping = ping;
        });
        while (ping == null) {
            Thread.onSpinWait();
        }
        event.reply(MessageFormat.format("My Ping to Discord: {0}ms\nMy Ping to you: {1}ms", gatewayPing, ping)).setEphemeral(true).queue();

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
