package qStivi.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import static qStivi.Bot.audioManager;

public class JoinCommand implements ICommand {

    public static void join(Guild guild, User author) {
        guild.getVoiceChannels().forEach(
                (channel) -> channel.getMembers().forEach(
                        (member) -> {
                            if (member.getId().equals(author.getId())) {
                                audioManager = guild.getAudioManager();
                                audioManager.openAudioConnection(channel);
                            }
                        }
                )
        );
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
}
