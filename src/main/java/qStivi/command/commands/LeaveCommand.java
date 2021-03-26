package qStivi.command.commands;

import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class LeaveCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        if (context.getGuild().getAudioManager() != null) context.getGuild().getAudioManager().closeAudioConnection();
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
