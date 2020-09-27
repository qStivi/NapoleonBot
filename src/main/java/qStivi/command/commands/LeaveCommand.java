package qStivi.command.commands;

import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import static qStivi.Bot.audioManager;

public class LeaveCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        if (audioManager != null) audioManager.closeAudioConnection();
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "Lets the Bot leave any voice channel.";
    }
}
