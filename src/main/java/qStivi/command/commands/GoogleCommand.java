package qStivi.command.commands;

import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

public class GoogleCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {

        String searchQuery = "";
        List<String> args = context.getArgs();
        // Collections.reverse(args);

        for (int i = 0; i < args.size(); i++) {
            searchQuery = searchQuery.concat(args.get(i));
            if (i < args.size() - 1) searchQuery = searchQuery.concat("%20");
        }

        context.getEvent().getChannel().sendMessage("http://www.justfuckinggoogleit.com/").queue();
    }

    @Override
    public String getName() {
        return "google";
    }

    @Override
    public String getHelp() {
        return "TODO";
    }
}
