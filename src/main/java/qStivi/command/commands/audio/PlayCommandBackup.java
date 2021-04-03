package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONException;
import org.json.JSONObject;
import qStivi.Spotify;
import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static qStivi.command.commands.JoinCommand.join;

public class PlayCommandBackup implements ICommand {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    @Override
    public void handle(CommandContext context) {

        Guild guild = context.getGuild();
        User author = context.getAuthor();
        TextChannel channel = context.getChannel();
        List<String> args = context.getArgs();

        if (context.getGuild().getAudioManager() == null) join(guild, author);
        if (!context.getGuild().getAudioManager().isConnected()) join(guild, author);

        String link = String.join("+", args);

        if (link.startsWith("https://open.spotify.com/track/")) {
            Spotify spotify = new Spotify();
            String id = link.substring(31, 53);
            link = spotify.getTrackArtists(id) + "+" + spotify.getTrackName(id);
            link = link.replace(" ", "+");
        } else if (link.startsWith("https://open.spotify.com/playlist/")) {
            Spotify spotify = new Spotify();
            String id = link.substring(34, 56);
//            System.out.println(id);
            List<String> names = spotify.getPlaylist(id);
            for (String name : names) {
                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&type=video&q=" + name + "&key=AIzaSyCUC5RBs-7hbODbk9OZAkx3HC6OWR-vDyY";

                JSONObject jsonObject = null;
                try {
                    jsonObject = readJsonFromUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                link = "https://youtu.be/" + Objects.requireNonNull(jsonObject).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");


                PlayerManager.getINSTANCE().loadAndPlay(guild, link);
                return;
            }
        }

        if (!isUrl(link)) {

            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&type=video&q=" + link + "&key=AIzaSyCUC5RBs-7hbODbk9OZAkx3HC6OWR-vDyY";

            JSONObject jsonObject = null;
            try {
                jsonObject = readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            link = "https://youtu.be/" + Objects.requireNonNull(jsonObject).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
        }

        PlayerManager.getINSTANCE().loadAndPlay(guild, link);
    }

    private boolean isUrl(String link) {
        if (link.startsWith("http")) {
            try {
                new URI(link);
                return true;
            } catch (URISyntaxException e) {
                return false;
            }
        } else return false;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Lets the bot join your current channel and adds the given song to the queue.";
    }
}
