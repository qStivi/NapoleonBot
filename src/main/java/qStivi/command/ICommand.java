package qStivi.command;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.List;

public interface ICommand {
    void handle(CommandContext context) throws IOException, ParseException, SpotifyWebApiException;

    String getName();

    String getHelp();

    default List<String> getAliases() {
        return List.of();
    }
}
