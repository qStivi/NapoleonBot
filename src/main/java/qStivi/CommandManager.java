package qStivi;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;
import qStivi.command.commands.*;
import qStivi.command.commands.audio.ContinueCommand;
import qStivi.command.commands.audio.PauseCommand;
import qStivi.command.commands.audio.PlayCommand;
import qStivi.command.commands.audio.SkipCommand;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new PingCommand());
        addCommand(new GoogleCommand());
        addCommand(new HelpCommand(this));
        addCommand(new PlayCommand());
        addCommand(new LeaveCommand());
        addCommand(new JoinCommand());
        addCommand(new SkipCommand());
        addCommand(new PauseCommand());
        addCommand(new ContinueCommand());
        addCommand(new RollCommand());
    }

    private void addCommand(ICommand command) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(command.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name already exists!");
        }

        commands.add(command);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        String lowerCaseSearch = search.toLowerCase();

        for (ICommand command : this.commands) {
            if (command.getName().equals(lowerCaseSearch) || command.getAliases().contains(lowerCaseSearch)) {
                return command;
            }
        }

        return null;
    }

    void handle(GuildMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand command = this.getCommand(invoke);

        //noinspection StatementWithEmptyBody
        if (command != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext context = new CommandContext(event, args);

            command.handle(context);
        } else {
            // TODO tell user command was not found.
        }
    }

}
