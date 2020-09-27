package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = getLogger(Listener.class);
    private final CommandManager commandManager = new CommandManager();

    String prefix = Config.get("PREFIX");
    String ownerId = Config.get("OWNER_ID");
    String channelId = Config.get("CHANNEL_ID");

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("Ready!");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "stop") && event.getAuthor().getId().equals(ownerId)) {

            LOGGER.info("Shutting down...");

            JDA jda = event.getJDA();
            OkHttpClient httpClient = jda.getHttpClient();
            httpClient.connectionPool().evictAll();
            httpClient.dispatcher().executorService().shutdownNow();
            jda.shutdown();

            return;
        }

        Objects.requireNonNull(event.getGuild().getTextChannelById(channelId)).getManager().setName(String.valueOf(event.getGuild().getMemberCount())).queue();
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;

        String messageRaw = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        if (messageRaw.startsWith(prefix)) commandManager.handle(event);

        /*
        Reactions
         */
        if (messageRaw.toLowerCase().startsWith("ree") && event.getChannel().getId().equals(channelId)) {
            String[] words = messageRaw.split("\\s+");
            String ree = words[0];
            String[] ees = ree.split("[rR]");
            try {
                channel.sendMessage(ree + ees[1] + ees[1]).queue();
            } catch (IllegalArgumentException ex) {
                channel.sendMessage("r" + ees[1]).queue();
            }
        }

        if (messageRaw.toLowerCase().startsWith("hmm")) {
            event.getMessage().addReaction("U+1F914").queue();
        }

    }
}
