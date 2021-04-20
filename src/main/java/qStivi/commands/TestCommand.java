package qStivi.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.time.Duration;

public class TestCommand implements ICommand {

    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("k").delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
    }

    @Override
    public @Nonnull
    String getName() {
        return "test";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "This is a test command";
    }
}
