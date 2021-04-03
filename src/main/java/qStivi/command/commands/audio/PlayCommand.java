package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import qStivi.YouTubeAPI;
import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static qStivi.command.commands.JoinCommand.join;

@SuppressWarnings({"unused", "CommentedOutCode"})
public class PlayCommand implements ICommand {


    private static final Logger logger = getLogger(PlayCommand.class);

    @Override
    public void handle(CommandContext context) {

        // Check if sender has joined a channel
        if (!join(context.getGuild(), context.getAuthor())) {
            context.getChannel().sendMessage("Please join a channel, so I can play your request.").queue();
            return;
        }

        // Get command arguments
        String arg0 = context.getArgs().get(0);
        Boolean randomizeOrder = false;
        try {
            if (context.getArgs().get(1).toLowerCase().contains("random") || context.getArgs().get(1).toLowerCase().contains("shuffle")) randomizeOrder = true;
        } catch (Exception ignored) {
        }

        RequestType requestType = getRequestType(arg0);

        if (requestType == RequestType.YOUTUBE) {
            YoutubeType youtubeType = getYouTubeType(arg0);
            if (youtubeType == YoutubeType.TRACK) {
                playYoutubeTrack(arg0, context.getGuild());
            } else if (youtubeType == YoutubeType.PLAYLIST) {
                try {
                    playYoutubePlaylist(arg0, randomizeOrder, context.getChannel());
                } catch (IOException e) {
                    context.getChannel().sendMessage(e.getMessage()).queue();
                    e.printStackTrace();
                }
            } else {
                logger.error("Something went wrong!");
            }
        } else if (requestType == RequestType.SPOTIFY) {
            SpotifyType spotifyType = getSpotifyType(arg0);
            if (spotifyType == SpotifyType.TRACK) {
                playSpotifyTrack(arg0);
            } else if (spotifyType == SpotifyType.PLAYLIST) {
                playSpotifyPlaylist(arg0, randomizeOrder);
            } else if (spotifyType == SpotifyType.ALBUM) {
                playSpotifyAlbum(arg0, randomizeOrder);
            } else if (spotifyType == SpotifyType.ARTIST) {
                playSpotifyArtist(arg0, randomizeOrder);
            } else {
                logger.error("Something went wrong!");
            }
        } else if (requestType == RequestType.SEARCH) {

            // Combine words
            StringBuilder search = new StringBuilder();
            for (int i = 0; i <= context.getArgs().size() - 1; i++) {
                search.append(context.getArgs().get(i));
                if (i < context.getArgs().size() - 1) search.append("+");
            }

            // search play
            try {
                searchPlay(search.toString(), context.getChannel());
            } catch (IOException e) {
                context.getChannel().sendMessage(e.getMessage()).queue();
                e.printStackTrace();
            }
        } else {
            logger.error("Something went wrong!");
        }
    }

    private void playSpotifyArtist(String arg0, Boolean randomizeOrder) {

        logger.error("NOT YET IMPLEMENTED!");
    }

    private void playSpotifyAlbum(String arg0, Boolean randomizeOrder) {

        logger.error("NOT YET IMPLEMENTED!");
    }

    private void playSpotifyPlaylist(String arg0, Boolean randomizeOrder) {

        logger.error("NOT YET IMPLEMENTED!");
    }

    private void playSpotifyTrack(String arg0) {

        logger.error("NOT YET IMPLEMENTED!");
    }

    private SpotifyType getSpotifyType(String arg0) {
        logger.error("NOT YET IMPLEMENTED!");
        return null;
    }

    private void playYoutubePlaylist(String link, Boolean randomizeOrder, TextChannel channel) throws IOException {

        List<String> ids = YouTubeAPI.getPlaylistItemsByLink(link);
        if (randomizeOrder) Collections.shuffle(ids);
        for (String id : ids) {
            PlayerManager.getINSTANCE().loadAndPlay(channel.getGuild(), "https://youtu.be/" + id);
        }
        channel.sendMessage("Added " + ids.size() + " songs to the queue.").queue();
    }

    private void playYoutubeTrack(String url, Guild guild) {
        PlayerManager.getINSTANCE().loadAndPlay(guild, url);
    }

    private YoutubeType getYouTubeType(String link) {
        if (link.contains("youtube.com/watch?v=") || link.contains("youtu.be/")) {
            return YoutubeType.TRACK;
        } else if (link.contains("youtube.com/playlist?list=")) {
            return YoutubeType.PLAYLIST;
        }
        logger.error("Something went wrong!");
        return null;
    }

    private RequestType getRequestType(String arg0) {
        if (isValidLink(arg0)) {
            if (arg0.contains("youtube") || arg0.contains("youtu.be")) {
                return RequestType.YOUTUBE;
            } else if (arg0.contains("spotify")) {
                return RequestType.SPOTIFY;
            }
        } else {
            return RequestType.SEARCH;
        }
        logger.error("Something went wrong!");
        return null;
    }

    private boolean isValidLink(String link) {
        return link.matches("(.*)open.spotify.com(.*)|spotify(.*)|(.*)youtube.com(.*)|(.*)youtu.be(.*)");
    }

    private void searchPlay(String search, TextChannel channel) throws IOException {
        String id = YouTubeAPI.getVideoIdBySearchQuery(search);
        String link = "https://youtu.be/" + id;
        channel.sendMessage(link).queue();
        PlayerManager.getINSTANCE().loadAndPlay(channel.getGuild(), link);
    }

//    private void spotifyTrack(String link, Guild guild) {
//        Spotify spotify = new Spotify();
//        String id = link.substring(31, 53);
//        String string = spotify.getTrackArtists(id) + "+" + spotify.getTrackName(id);
//        string = string.replace(" ", "+");
//        String search = search(string);
//        PlayerManager.getINSTANCE().loadAndPlay(guild, search);
//    }

    private boolean isURL(String s) {
        try {
            new URL(s).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Plays music for you! \n Usage: `/p <link>` `/play <search term>` \n `/pl <link to playlist>` `/p <link to playlist> shuffle` \n `/p <link to playlist> random`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("p", "pl", "pla", "paly");
    }

    private enum RequestType {
        YOUTUBE,
        SPOTIFY,
        SEARCH
    }

    private enum YoutubeType {
        TRACK,
        PLAYLIST
    }

    private enum SpotifyType {
        TRACK,
        PLAYLIST,
        ALBUM,
        ARTIST
    }
}
