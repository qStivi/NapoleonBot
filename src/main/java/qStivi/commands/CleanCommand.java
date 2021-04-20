package qStivi.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import qStivi.ICommand;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CleanCommand implements ICommand {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();
        List<Message> messages = new ArrayList<>();
        var option = args.length >= 1 && Boolean.parseBoolean(args[1]);
        if (option) {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                messages = event.getChannel().getIterableHistory().stream().limit(1000).collect(Collectors.toList());
            } else {
                hook.sendMessage("You don't have the permissions to do that.").queue();
            }
        } else {
            messages = event.getChannel().getIterableHistory().stream().limit(1000).filter(message -> message.getAuthor().getId().equals(event.getAuthor().getId())).collect(Collectors.toList());
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
