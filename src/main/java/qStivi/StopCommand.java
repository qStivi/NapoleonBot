package qStivi;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.audioManagers.PlayerManager;

import javax.annotation.Nonnull;
import java.time.Duration;

public class StopCommand implements INewCommand {
    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        PlayerManager.getINSTANCE().clearQueue(event.getGuild());
        PlayerManager.getINSTANCE().skip(event.getGuild());
        event.reply("Playback stopped.").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();
    }

    @Override
    public @Nonnull
    String getName() {
        return "stop";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Stop music from playing and clears queue";
    }
}
