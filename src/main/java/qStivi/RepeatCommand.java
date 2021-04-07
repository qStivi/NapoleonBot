package qStivi;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.audioManagers.PlayerManager;

import javax.annotation.Nonnull;
import java.time.Duration;

public class RepeatCommand implements INewCommand {
    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandEvent event) {
        PlayerManager playerManager = PlayerManager.getINSTANCE();
        playerManager.setRepeat(event.getGuild(), !playerManager.isRepeating(event.getGuild()));
        if (playerManager.isRepeating(event.getGuild())) {
            event.reply("Repeat: ON").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();
        } else {
            event.reply("Repeat: OFF").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();
        }
    }

    @Override
    public @Nonnull
    String getName() {
        return "repeat";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Toggles repeating for playing song";
    }
}
