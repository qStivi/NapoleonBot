package qStivi.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CleanCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        if(Objects.requireNonNull(context.getMessage().getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            List<Message> messages = new ArrayList<>();

            context.getChannel().getIterableHistory().stream().limit(50).forEach(messages::add);

            context.getChannel().purgeMessages(messages);
        }
    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public String getHelp() {
        return "Deletes 50 last messages.";
    }
}
