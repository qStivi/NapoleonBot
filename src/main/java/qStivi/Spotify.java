package qStivi;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Spotify {
    private static final String clientId = Config.get("SPOTIFY_ID");
    private static final String clientSecret = Config.get("SPOTIFY_SECRET");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();

    public Spotify() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

//            System.out.println("Token: " + clientCredentials.getAccessToken());
//            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getTrackName(String id) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(id).build();
        try {
            final Track track = getTrackRequest.execute();
            return track.getName();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public String getTrackArtists(String id) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(id).build();
        try {
            final Track track = getTrackRequest.execute();
            return Arrays.stream(track.getArtists()).findFirst().get().getName();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    // https://developer.spotify.com/console/get-playlist-tracks/?playlist_id=3cEYpjA9oz9GiPac4AsH4n&market=ES&fields=items(added_by.id%2Ctrack(name%2Chref%2Calbum(name%2Chref)))&limit=10&offset=5&additional_types=
    public List<String> getPlaylist(String id) {
        InputStream response = null;
        List<String> finalOutput = new ArrayList<>();
        try {
            URLConnection connection = new URL("https://api.spotify.com/v1/playlists/" + id + "/tracks?market=DE&fields=items(track(name%2C%20artists(name)))").openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + spotifyApi.getAccessToken());
            response = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Scanner scanner = new Scanner(Objects.requireNonNull(response))) {
            String responseBody = scanner.useDelimiter("\\A").next();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray items = jsonObject.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject track = item.getJSONObject("track");
                String name = track.getString("name");
//                System.out.println(name);
                String names = "";
                JSONArray artists = track.getJSONArray("artists");
                for (int j = 0; j < artists.length(); j++) {
                    JSONObject object = artists.getJSONObject(j);
                    names = names.concat("+" + object.getString("name"));
                }
//                System.out.println(names);
                String output = name + names;
                output = output.replace(" ", "+");
//                System.out.println(output);
                finalOutput.add(output);
            }
        }
        return finalOutput;
    }


}