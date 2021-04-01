package qStivi.command.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class RedditCommand implements ICommand {

    private static final Logger logger = getLogger(RedditCommand.class);

//    UserAgent userAgent = new UserAgent("Discord Bot", "qstivi.napoleon", "1", "qStivi");
//    Credentials credentials = Credentials.script("qStivi", Config.get("REDDIT_PASSWORD"), Config.get("REDDIT_ID"), Config.get("REDDIT_SECRET"));
//    NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
//    RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

    @Override
    public void handle(CommandContext context) {

        TextChannel channel = context.getChannel();

        String searchQuery = "";
        String url;

        List<String> args = context.getArgs();
        for (int i = 0; i < args.size(); i++) {
            searchQuery = searchQuery.concat(args.get(i));
            if (i < args.size() - 1) searchQuery = searchQuery.concat("");
        }
        if (searchQuery.length() < 1) return; // TODO do something useful here...
//
//        RootCommentNode randomSubmission = reddit.subreddit(searchQuery).randomSubmission();
//        Submission submissionSubject = randomSubmission.getSubject();
//
//        url = submissionSubject.getUrl();
//        String link = submissionSubject.getUrl();
//
//        logger.info(link);
//        if (link.contains("i.redd.it") || link.contains("v.redd.it") || link.contains("youtu.be") || link.contains("youtube.com") || link.contains("imgur.com") || link.contains("giphy.com") || link.contains("gfycat.com")) {
//
//            if (link.contains("i.redd.it")) {
//                sendFancyTitle(channel, submissionSubject);
//                channel.sendMessage(url).queue();
//            } else if (link.contains("v.redd.it")) {
//                if (submissionSubject.getEmbeddedMedia() != null) {
//                    if (submissionSubject.getEmbeddedMedia().getRedditVideo() != null) {
//                        sendFancyTitle(channel, submissionSubject);
//                        channel.sendMessage(submissionSubject.getEmbeddedMedia().getRedditVideo().getFallbackUrl()).queue();
//                    }
//                } else permalink(context, randomSubmission); // This is usually a cross post
//
//            } else {
//                sendFancyTitle(channel, submissionSubject);
//                channel.sendMessage(url).queue();
//            }
//
//        } else {
//            permalink(context, randomSubmission);
//        }
    }

//    private void sendFancyTitle(TextChannel channel, Submission submissionSubject) {
//        String postLink = "https://reddit.com";
//        postLink = postLink.concat(submissionSubject.getPermalink());
//        channel.sendMessage(
//                new EmbedBuilder()
//                        .setTitle(submissionSubject.getTitle(), postLink)
//                        .setAuthor(submissionSubject.getSubreddit())
//                        .setFooter(submissionSubject.getScore() + " votes and " + submissionSubject.getCommentCount() + " comments")
//                        .build()
//        ).queue();
//    }

//    private void permalink(CommandContext context, RootCommentNode randomSubmission) {
//        String postLink = "https://reddit.com";
//        postLink = postLink.concat(randomSubmission.getSubject().getPermalink());
//        context.getChannel().sendMessage(postLink).queue();
//    }

    @Override
    public String getName() {
        return "reddit";
    }

    @Override
    public String getHelp() {
        return "TODO";
    }

    @Override
    public List<String> getAliases() {
        return List.of("red");
    }
}
