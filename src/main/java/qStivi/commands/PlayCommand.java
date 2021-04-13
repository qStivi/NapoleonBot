package qStivi.commands;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.ICommand;
import qStivi.apis.Spotify;
import qStivi.apis.YouTube;
import qStivi.audioManagers.PlayerManager;
import qStivi.listeners.ControlsManager;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;
import static qStivi.commands.JoinCommand.join;

public class PlayCommand implements ICommand {


    private static final Logger logger = getLogger(PlayCommand.class);

    public static String cleanForURL(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFKD);
        str = str.replaceAll("[^a-z0-9A-Z -]", ""); // Remove all non valid chars
        str = str.replaceAll(" {2}", " ").trim(); // convert multiple spaces into one space
        str = str.replaceAll(" ", "+"); // //Replace spaces by dashes
        return str;
    }

    public String playSong(String song, boolean shuffle, TextChannel channel, Guild guild) throws ParseException, SpotifyWebApiException, IOException {


        RequestType requestType = getRequestType(song);

        if (requestType == RequestType.YOUTUBE) {
            YoutubeType youtubeType = getYouTubeType(song);
            if (youtubeType != null) {
                switch (youtubeType) {
                    case TRACK -> song = playYoutubeTrack(song, channel, guild);
                    case PLAYLIST -> song = playYoutubePlaylist(song, shuffle, channel);
                }
            }
        } else if (requestType == RequestType.SPOTIFY) {
            SpotifyType spotifyType = getSpotifyType(song);
            if (spotifyType != null) {
                switch (spotifyType) {
                    case TRACK -> song = playSpotifyTrack(song, channel);
                    case PLAYLIST -> song = playSpotifyPlaylist(song, shuffle);
                    case ALBUM -> song = playSpotifyAlbum(song, shuffle);
                    case ARTIST -> song = playSpotifyArtist(song, shuffle);
                }
            }
        } else if (requestType == RequestType.SEARCH) {
            song = searchPlay(song, channel);
        }
        if (song != null) ControlsManager.getINSTANCE().sendMessage(channel, guild);

        return song;
    }

    private String playSpotifyArtist(String arg0, Boolean randomizeOrder) {

        logger.info(arg0);
        logger.error("NOT YET IMPLEMENTED!");
        return null;
    }

    private String playSpotifyAlbum(String arg0, Boolean randomizeOrder) {

        logger.error("NOT YET IMPLEMENTED!");
        return null;
    }

    private String playSpotifyPlaylist(String arg0, Boolean randomizeOrder) {

        logger.error("NOT YET IMPLEMENTED!");
        return null;
    }

    private String playSpotifyTrack(String link, TextChannel channel) throws IOException, ParseException, SpotifyWebApiException {

        if (link.contains("open.spotify.com/track/")) {
            link = link.replace("https://", "");
            link = link.split("/")[2];
            link = link.split("\\?")[0];
        } else if (link.startsWith("spotify:track:")) {
            link = link.split(":")[2];
        }

        Spotify spotify = new Spotify();
        String search = spotify.getTrackArtists(link) + " " + spotify.getTrackName(link);
        search = searchPlay(search, channel);
        return search;
    }

    private SpotifyType getSpotifyType(String link) {
        if (link.contains("track")) {
            return SpotifyType.TRACK;
        } else if (link.contains("playlist")) {
            return SpotifyType.PLAYLIST;
        } else if (link.contains("album")) {
            return SpotifyType.ALBUM;
        } else if (link.contains("artist") || link.contains("\uD83E\uDDD1\u200D\uD83C\uDFA8")) {
            return SpotifyType.ARTIST;
        }
        return null;
    }

    private String playYoutubePlaylist(String link, Boolean randomizeOrder, TextChannel channel) throws IOException {

        List<String> ids = YouTube.getPlaylistItemsByLink(link);
        if (randomizeOrder) Collections.shuffle(ids);
        for (String id : ids) {
            PlayerManager.getINSTANCE().loadAndPlay(channel, channel.getGuild(), "https://youtu.be/" + id);
        }
        channel.sendMessage("Added " + ids.size() + " songs to the queue.").queue();
        return link;
    }

    private String playYoutubeTrack(String url, TextChannel channel, Guild guild) {
        PlayerManager.getINSTANCE().loadAndPlay(channel, guild, url);
        return url;
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

    private String searchPlay(String search, TextChannel channel) throws IOException {
        search = cleanForURL(search);
        String id = YouTube.getVideoIdBySearchQuery(search);
        String link = "https://youtu.be/" + id;
        PlayerManager.getINSTANCE().loadAndPlay(channel, channel.getGuild(), link);
        return link;
    }

    @NotNull
    @Override
    public CommandUpdateAction.CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING, "query", "The song you want to play. Can be a link or search query.")
                        .setRequired(true))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.BOOLEAN, "shuffle", "Do you want the playlist to be shuffled?")
                        .setRequired(false));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        if (!join(event.getGuild(), event.getUser())) {
            hook.sendMessage("Please join a channel, so I can play your request.").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
            return;
        }
        try {
            if (event.getOption("shuffle") != null) {
                if (event.getOption("shuffle").getAsBoolean()) {
                    hook.sendMessage(playSong(event.getOption("query").getAsString(), true, event.getTextChannel(), event.getGuild())).delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
                }
            } else {
                var msg = playSong(event.getOption("query").getAsString(), false, event.getTextChannel(), event.getGuild());
                hook.sendMessage(Objects.requireNonNullElse(msg, "Something went wrong!")).delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
            }
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
            hook.sendMessage("Something went wrong :(").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "play";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Plays music.";
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
