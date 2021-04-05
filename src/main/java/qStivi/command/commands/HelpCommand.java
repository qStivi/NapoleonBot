package qStivi.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import qStivi.CommandManager;
import qStivi.Config;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.io.IOException;
import java.util.List;

public class HelpCommand implements ICommand {


    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext context) throws IOException {

        List<String> args = context.getArgs();
        TextChannel channel = context.getChannel();

        if (args.isEmpty()) {

            EmbedBuilder builder = new EmbedBuilder();

            manager.getCommands().forEach(
                    (command) -> builder.addField(
                            new MessageEmbed.Field(
                                    Config.get("PREFIX") + command.getName(),
                                    command.getHelp(),
                                    false))
            );

            channel.sendMessage(builder.build()).queue();

            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("There is no command: " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle(Config.get("PREFIX") + command.getName())
                        .setDescription(command.getHelp())
                        .build()
        ).queue();

    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Provides help for all commands or all commands";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "befehle", "list");
    }
}
