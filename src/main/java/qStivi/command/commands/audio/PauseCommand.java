package qStivi.command.commands.audio;

import qStivi.audio.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class PauseCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        PlayerManager.getINSTANCE().pause(context.getChannel());
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return "Pauses the currently playing song until a new one is queued or it is resumed.";
    }
}
