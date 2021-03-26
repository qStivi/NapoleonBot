package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import qStivi.command.commands.audio.ControlsManager;

import javax.security.auth.login.LoginException;

import static org.slf4j.LoggerFactory.getLogger;

public class Bot {

    private static final Logger logger = getLogger(Bot.class);

    private Bot() {
        try {
            JDABuilder.createDefault(Config.get("TOKEN"))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(new Listener())
                    .addEventListeners(new ControlsManager())
                    .setActivity(Activity.listening("/help for more info..."))
                    .build();
        } catch (LoginException e) {
            logger.error("Error trying to login!");
        }
    }

    public static void main(String[] args) {
        new Bot();
    }
}
