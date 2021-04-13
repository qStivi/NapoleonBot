package qStivi.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CleanCommand implements ICommand {

    @Override
    @Nonnull
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            List<Message> messages = new ArrayList<>();

            event.getChannel().getIterableHistory().stream().limit(1000).forEach(messages::add);

            event.getChannel().purgeMessages(messages);

            hook.sendMessage("Cleaning...").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
        } else {
            hook.sendMessage("You don't have the permissions to do that.").setEphemeral(true).queue();
        }
    }

    @Override
    public @Nonnull
    String getName() {
        return "clean";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Deletes last 1000 messages. This takes quite some time.";
    }
}
