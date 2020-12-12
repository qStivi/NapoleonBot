package qStivi.command.commands.audio;

import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class ContinueCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        PlayerManager.getINSTANCE().continueTrack(context.getChannel());
    }

    @Override
    public String getName() {
        return "continue";
    }

    @Override
    public String getHelp() {
        return "Continues the paused Track.";
    }
}
