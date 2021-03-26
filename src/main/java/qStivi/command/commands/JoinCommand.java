package qStivi.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JoinCommand implements ICommand {

    public static boolean join(Guild guild, User author) {
        AtomicBoolean successful = new AtomicBoolean(false);
        guild.getVoiceChannels().forEach(
                (channel) -> channel.getMembers().forEach(
                        (member) -> {
                            if (member.getId().equals(author.getId())) {
                                //audioManager = guild.getAudioManager();
                                guild.getAudioManager().openAudioConnection(channel);
                                successful.set(true);
                            }
                        }
                )
        );
        return successful.get();
    }

    @Override
    public void handle(CommandContext context) {

        Guild guild = context.getGuild();
        User author = context.getAuthor();

        join(guild, author);

    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getHelp() {
        return "Lets the Bot join your voice channel.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("summon");
    }
}
