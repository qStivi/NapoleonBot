package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;
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

import static qStivi.Bot.audioManager;
import static qStivi.command.commands.JoinCommand.join;

public class PlayCommand implements ICommand {
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

        connect(context);

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
                youtubePlaylist(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/track")) {
                spotifyTrack(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/album")) {
                spotifyAlbum(args.toString(), context.getChannel());
            } else if (trackURL.contains("spotify.com/playlist")) {
                spotifyPlaylist(args.toString(), context.getChannel());
            } else {
                context.getChannel().sendMessage("Sorry, but I can't do that.").queue();
                context.getChannel().sendMessage(trackURL).queue();
            }
        } else {
            searchPlay(args.toString(), context.getChannel());
        }
    }

    private void spotifyPlaylist(String link, TextChannel channel) {
        // TODO
    }

    private void spotifyAlbum(String link, TextChannel channel) {
        // TODO
    }

    private void youtubePlaylist(String link, TextChannel channel) {
        // TODO
    }

    private void searchPlay(String search, TextChannel channel) {
        PlayerManager.getINSTANCE().loadAndPlay(channel, search(search));
    }

    private void connect(CommandContext context) {

        if (audioManager == null) join(context.getGuild(), context.getAuthor());
        if (!audioManager.isConnected()) join(context.getGuild(), context.getAuthor());
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
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&type=video&q=" + string + "&key=AIzaSyCUC5RBs-7hbODbk9OZAkx3HC6OWR-vDyY";

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
