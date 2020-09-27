package qStivi.command.commands.audio;

import qStivi.audio.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

public class SkipCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        PlayerManager.getINSTANCE().skip(context.getChannel());
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Starts to play next song immediately.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("next");
    }
}
