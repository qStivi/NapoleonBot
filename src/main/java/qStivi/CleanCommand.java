package qStivi;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CleanCommand implements INewCommand {

    @Override
    @Nonnull
    public CommandUpdateAction.@NotNull CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            List<Message> messages = new ArrayList<>();

            event.getChannel().getIterableHistory().stream().limit(50).forEach(messages::add);

            event.reply("Cleaning...").delay(Duration.ofSeconds(60)).flatMap(CommandHook::deleteOriginal).queue();

            event.getChannel().purgeMessages(messages);


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
        return "Deletes last 50 messages.";
    }
}
