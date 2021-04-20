package qStivi.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;

import java.time.Duration;
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();

        Guild guild = event.getGuild();
        User author = event.getMember().getUser();

        var success = join(guild, author);
        if (success) {
            hook.sendMessage("Hi").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
        } else {
            hook.sendMessage("Something went wrong :(").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
        }

    }

    @NotNull
    @Override
    public String getName() {
        return "join";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Connects or moves bot to your voice channel.";
    }
}
