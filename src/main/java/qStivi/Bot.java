package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import qStivi.listeners.ControlsManager;
import qStivi.listeners.Listener;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Listener())
                .setActivity(Activity.playing("New commands YAY!"))
                .build();

        jda.addEventListener(new CommandManager(jda));
    }

}