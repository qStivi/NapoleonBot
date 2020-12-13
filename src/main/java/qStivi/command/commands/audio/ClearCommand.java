package qStivi.command.commands.audio;

import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class ClearCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        PlayerManager pm = PlayerManager.getINSTANCE(context.getGuild());
        pm.clearQueue();
        pm.skip();
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getHelp() {
        return "Clears all current playing tracks.";
    }
}
