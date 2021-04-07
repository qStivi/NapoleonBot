package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import qStivi.command.commands.audio.ControlsManager;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Listener())
                .setActivity(Activity.competing("New commands YAY!"))
                .build();

        jda.addEventListener(new NewCommandManager(jda));
    }

}