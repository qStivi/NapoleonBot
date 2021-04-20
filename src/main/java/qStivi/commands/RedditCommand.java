package qStivi.commands;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.tree.RootCommentNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.Config;
import qStivi.ICommand;

import javax.annotation.CheckReturnValue;
import java.text.Normalizer;

import static org.slf4j.LoggerFactory.getLogger;

public class RedditCommand implements ICommand {

    private static final Logger logger = getLogger(RedditCommand.class);

    UserAgent userAgent = new UserAgent("Discord Bot", "qstivi.napoleon", "1", "qStivi");
    Credentials credentials = Credentials.script("qStivi", Config.get("REDDIT_PASSWORD"), Config.get("REDDIT_ID"), Config.get("REDDIT_SECRET"));
    NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
    RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(GuildMessageReceivedEvent event, String[] args) {
        var hook = event.getChannel();

        String url;

        var subreddit = args[1];
        subreddit = Normalizer.normalize(subreddit, Normalizer.Form.NFKD);
        subreddit = subreddit.replaceAll("[^a-z0-9A-Z -]", ""); // Remove all non valid chars
        subreddit = subreddit.replaceAll(" {2}", " ").trim(); // convert multiple spaces into one space
        subreddit = subreddit.replaceAll(" ", ""); // //Replace spaces by nothing

        RootCommentNode randomSubmission = reddit.subreddit(subreddit).randomSubmission();
        Submission submissionSubject = randomSubmission.getSubject();

        url = submissionSubject.getUrl();
        String link = submissionSubject.getUrl();

        logger.info(link);
        if (link.contains("i.redd.it") || link.contains("v.redd.it") || link.contains("youtu.be") || link.contains("youtube.com") || link.contains("imgur.com") || link.contains("giphy.com") || link.contains("gfycat.com")) {

            if (link.contains("i.redd.it")) {
                hook.sendMessage(sendFancyTitle(submissionSubject)).queue();
                event.getChannel().sendMessage(url).queue();
            } else if (link.contains("v.redd.it")) {
                if (submissionSubject.getEmbeddedMedia() != null) {
                    if (submissionSubject.getEmbeddedMedia().getRedditVideo() != null) {
                        hook.sendMessage(sendFancyTitle(submissionSubject)).queue();
                        event.getChannel().sendMessage(submissionSubject.getEmbeddedMedia().getRedditVideo().getFallbackUrl()).queue();
                    }
                } else hook.sendMessage(permalink(randomSubmission)).queue(); // This is usually a cross post

            } else {
                hook.sendMessage(sendFancyTitle(submissionSubject)).queue();
                event.getChannel().sendMessage(url).queue();
            }

        } else {
            hook.sendMessage(permalink(randomSubmission)).queue();
        }
    }

    @CheckReturnValue
    private MessageEmbed sendFancyTitle(Submission submissionSubject) {
        String postLink = "https://reddit.com";
        postLink = postLink.concat(submissionSubject.getPermalink());
        return new EmbedBuilder()
                .setTitle(submissionSubject.getTitle(), postLink)
                .setAuthor(submissionSubject.getSubreddit())
                .setFooter(submissionSubject.getScore() + " votes and " + submissionSubject.getCommentCount() + " comments")
                .build();
    }

    @CheckReturnValue
    private String permalink(RootCommentNode randomSubmission) {
        String postLink = "https://reddit.com";
        postLink = postLink.concat(randomSubmission.getSubject().getPermalink());
        return postLink;
    }

    @Override
    public @NotNull String getName() {
        return "reddit";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Sends random post from given subreddit.";
    }
}
