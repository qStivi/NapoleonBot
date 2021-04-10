package qStivi.apis;

import org.json.JSONArray;
import org.json.JSONObject;
import qStivi.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class YouTube {

    private static final String SECRET = Config.get("YOUTUBE_KEY");

    public static String getVideoIdBySearchQuery(String searchQuery) throws IOException {
        String query = "https://youtube.googleapis.com/youtube/v3/search?part=id&maxResults=1&q=" + searchQuery + "&safeSearch=none&type=video&key=" + SECRET;
        JSONObject jsonObject = readJsonFromUrl(query);
        return jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
    }

    public static List<String> getPlaylistItemsByLink(String link) throws IOException {
        String[] strings = link.split("list=");
        String id = strings[1];
        String query = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=50&playlistId=" + id + "&key=" + SECRET;
        JSONObject jsonObject = readJsonFromUrl(query);
        JSONArray items = jsonObject.getJSONArray("items");
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            videoIds.add(items.getJSONObject(i).getJSONObject("contentDetails").getString("videoId"));
        }
        return videoIds;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            return new JSONObject(jsonText);
        }
    }
}