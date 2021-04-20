package qStivi;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

public interface ICommand {

    void handle(GuildMessageReceivedEvent event, String[] args);

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();
}
