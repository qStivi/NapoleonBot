package qStivi.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CleanCommand implements ICommand {

    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.BOOLEAN, "all", "Do you want to clean all messages? Not only yours.")
                        .setRequired(false));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        List<Message> messages = new ArrayList<>();
        var option = event.getOption("all");
        if (option != null && option.getAsBoolean()) {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                messages = event.getChannel().getIterableHistory().stream().limit(1000).collect(Collectors.toList());
            } else {
                hook.sendMessage("You don't have the permissions to do that.").setEphemeral(true).queue();
            }
        } else {
            messages = event.getChannel().getIterableHistory().stream().limit(1000).filter(message -> message.getAuthor().getId().equals(event.getUser().getId())).collect(Collectors.toList());
        }
        event.getChannel().purgeMessages(messages);
        hook.sendMessage("Cleaning...").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
    }

    @Nonnull
    @Override
    public String getName() {
        return "clean";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Deletes last 1000 messages. This takes quite some time.";
    }
}
