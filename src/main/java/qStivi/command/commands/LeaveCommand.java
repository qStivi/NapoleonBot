package qStivi.command.commands;

import qStivi.command.CommandContext;
import qStivi.command.ICommand;

public class LeaveCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        context.getGuild().getAudioManager();
        context.getGuild().getAudioManager().closeAudioConnection();
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
