package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import qStivi.audio.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

import static qStivi.Bot.audioManager;
import static qStivi.command.commands.JoinCommand.join;

public class PlayCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {

        Guild guild = context.getGuild();
        User author = context.getAuthor();
        TextChannel channel = context.getChannel();
        List<String> args = context.getArgs();

        if (audioManager == null) join(guild, author);
        if (!audioManager.isConnected()) join(guild, author);
        PlayerManager.getINSTANCE().loadAndPlay(channel, args.get(0).strip());
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Lets the bot join your current channel and adds the given song to the queue.";
    }
}
