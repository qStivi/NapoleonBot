package qStivi;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.command.commands.audio.ControlsManager;

import java.io.IOException;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class Listener extends ListenerAdapter {

    private static final Logger logger = getLogger(Listener.class);
    private final CommandManager commandManager = new CommandManager();

    String prefix = Config.get("PREFIX");
    String ownerId = Config.get("OWNER_ID");
    String channelId = Config.get("CHANNEL_ID");

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Ready!");
        Objects.requireNonNull(event.getJDA().getTextChannelById(channelId)).sendMessage("Ready!").queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        // User user = event.getMessage().getMentionedMembers().get(0).getUser();

        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(ownerId)) {

            event.getChannel().sendMessage("https://tenor.com/view/pc-computer-shutting-down-off-windows-computer-gif-17192330").queue();

            logger.info("Shutting down...");

            ControlsManager.getINSTANCE().deleteMessage();

            event.getJDA().shutdownNow();

            System.exit(0);


            return;
        }

        Objects.requireNonNull(event.getGuild().getTextChannelById(channelId)).getManager().setName(String.valueOf(event.getGuild().getMemberCount())).queue();
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;

        String messageRaw = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        if (messageRaw.startsWith(prefix)) {
            try {
                commandManager.handle(event);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

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
