package qStivi.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.Config;

import java.time.Duration;

import static org.slf4j.LoggerFactory.getLogger;

public class Listener extends ListenerAdapter {

    private static final Logger logger = getLogger(Listener.class);
//    private final CommandManager commandManager = new CommandManager();

    String channelId = Config.get("CHANNEL_ID");

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Ready!");
        event.getJDA().getGuildById("703363806356701295").getTextChannelById(channelId).sendMessage("Booting... Ready when message disappears.").delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        event.getGuild().getTextChannelById(channelId).getManager().setName(String.valueOf(event.getGuild().getMemberCount())).queue();
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;

        String messageRaw = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        /*
        Reactions
         */
        if (messageRaw.toLowerCase().startsWith("ree")) {
            String[] words = messageRaw.split("\\s+");
            String ree = words[0];
            String ees = ree.substring(1);
            channel.sendMessage(ree + ees + ees).queue();
        }

        if (messageRaw.toLowerCase().startsWith("hmm")) {
            event.getMessage().addReaction("U+1F914").queue();
        }
    }
}
