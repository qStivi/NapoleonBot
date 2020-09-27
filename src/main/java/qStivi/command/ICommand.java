package qStivi.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext context);

    String getName();

    String getHelp();

    default List<String> getAliases() {
        return List.of();
    }
}
