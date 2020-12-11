package qStivi.command.commands.audio;

import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

public class ControlsCommand implements ICommand {

    @Override
    public void handle(CommandContext context) {
        ControlsManager.getINSTANCE().sendMessage(context);
    }

    @Override
    public String getName() {
        return "controls";
    }

    @Override
    public String getHelp() {
        return "Shows the Music Controls";
    }

    @Override
    public List<String> getAliases() {
        return List.of("co", "con", "cont", "contr", "control");
    }
}
