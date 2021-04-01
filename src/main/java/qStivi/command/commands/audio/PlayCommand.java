package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;
import org.slf4j.Logger;
import qStivi.Config;
import qStivi.Spotify;
import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;
import static qStivi.command.commands.JoinCommand.join;

public class PlayCommand implements ICommand {


    private static final Logger logger = getLogger(PlayCommand.class);

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

    public static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            return new JSONObject(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void handle(CommandContext context) {

        if (!join(context.getGuild(), context.getAuthor())){
            context.getChannel().sendMessage("Please join a channel, so I can play your request.").queue();
            return;
        }

        String arg0 = context.getArgs().get(0);
//        String arg1 = context.getArgs().get(1);

        RequestType requestType = getRequestType(arg0);
/*
        if (requestType == RequestType.YOUTUBE) {
            YoutubeType youtubeType = getYouTubeType(arg0);
            if (youtubeType == YoutubeType.TRACK){
                playYoutubeTrack(arg0);
            } else if (youtubeType == YoutubeType.PLAYLIST){
                playYoutubePlaylist(arg0, arg1);
            } else {
                logger.error("Something went wrong!");
            }
        } else if (requestType == RequestType.SPOTIFY){
            SpotifyType spotifyType = getSpotifyType(arg0);
            if (spotifyType == SpotifyType.TRACK){
                playSpotifyTrack(arg0);
            }else if (spotifyType == SpotifyType.PLAYLIST){
                playSpotifyPlaylist(arg0, arg1);
            } else if (spotifyType == SpotifyType.ALBUM) {
                playSpotifyAlbum(arg0, arg1);
            } else if (spotifyType == SpotifyType.ARTIST) {
                playSpotifyArtist(arg0, arg1);
            } else {
                logger.error("Something went wrong!");
            }
        } else if (requestType == RequestType.SEARCH) {
            searchPlay(arg0);
        }else {
            logger.error("Something went wrong!");
        }

*/



















        StringBuilder args = new StringBuilder();
        if (context.getArgs().size() > 1) {
            for (String arg : context.getArgs()) {
                args.append("+").append(arg);
            }
        } else args.append(context.getArgs().get(0));

        String trackURL;
        if (isURL(args.toString())) {
            trackURL = args.toString();
            if (trackURL.contains("youtube.com/watch") || trackURL.contains("youtu.be")) {
                PlayerManager.getINSTANCE().loadAndPlay(context.getChannel(), trackURL);
            } else if (trackURL.contains("youtube.com/playlist")) {
//                youtubePlaylist(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/track")) {
                spotifyTrack(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/album")) {
//                spotifyAlbum(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/playlist")) {
//                spotifyPlaylist(args.toString(), context.getChannel());
            } else {
                context.getChannel().sendMessage("Sorry, but I can't do that.").queue();
                context.getChannel().sendMessage(trackURL).queue();
            }
        } else {
            searchPlay(args.toString(), context.getChannel());
        }
    }

    private YoutubeType getYouTubeType(String link) {
        if (link.contains("youtube.com/watch?v=") || link.contains("youtu.be/")) {
            return YoutubeType.TRACK;
        } else if (link.contains("youtube.com/playlist?list=")){
            return YoutubeType.PLAYLIST;
        }
        logger.error("Something went wrong!");
        return null;
    }

    private RequestType getRequestType(String arg0) {
        if (isValidLink(arg0)){
            if (arg0.contains("youtube") || arg0.contains("youtu.be")){
                return RequestType.YOUTUBE;
            } else if (arg0.contains("spotify")) {
                return RequestType.SPOTIFY;
            }
        }else {
            return RequestType.SEARCH;
        }
        logger.error("Something went wrong!");
        return null;
    }

    private boolean isValidLink(String link) {
        return link.matches("(.*)open.spotify.com(.*)|spotify(.*)|(.*)youtube.com(.*)|(.*)youtu.be(.*)");
    }

    private void searchPlay(String search, TextChannel channel) {
        PlayerManager.getINSTANCE().loadAndPlay(channel, search(search));
    }

    private void spotifyTrack(String link, TextChannel channel) {
        Spotify spotify = new Spotify();
        String id = link.substring(31, 53);
        String string = spotify.getTrackArtists(id) + "+" + spotify.getTrackName(id);
        string = string.replace(" ", "+");
        String search = search(string);
        PlayerManager.getINSTANCE().loadAndPlay(channel, search);
    }

    private String search(String string) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&type=video&q=" + string + "&key=" + Config.get("YOUTUBE_KEY");

        JSONObject jsonObject = readJsonFromUrl(url);

        return "https://youtu.be/" + Objects.requireNonNull(jsonObject).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
    }

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
        return "Plays music for you! \\n Usage: /p <link> or /play <search term>";
    }

    @Override
    public List<String> getAliases() {
        return List.of("p", "pl", "pla", "paly");
    }
}
