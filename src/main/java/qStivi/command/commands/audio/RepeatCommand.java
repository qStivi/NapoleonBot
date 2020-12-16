package qStivi.command.commands.audio;

import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class RepeatCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        PlayerManager playerManager = PlayerManager.getINSTANCE();
        playerManager.setRepeat(context.getGuild(), !playerManager.isRepeating(context.getGuild()));
        System.out.println(playerManager.isRepeating(context.getGuild()));
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getHelp() {
        return "toggles repeat of the current playing track";
    }
}
