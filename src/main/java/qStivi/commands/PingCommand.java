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
        var hook = event.getHook();
        long gatewayPing = event.getJDA().getGatewayPing();
        event.getJDA().getRestPing().queue((ping) -> {
            hook.sendMessage("Pinging...").flatMap(Message::delete).queue();
            this.ping = ping;
        });
        while (ping == null) {
            Thread.onSpinWait();
        }
        hook.editOriginal(MessageFormat.format("My Ping to Discord: {0}ms\nMy Ping to you: {1}ms", gatewayPing, ping)).delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();

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
