package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import qStivi.commands.BlackjackCommand;
import qStivi.db.DB;
import qStivi.listeners.ControlsManager;
import qStivi.listeners.Listener;
import qStivi.listeners.UserManager;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Bot {
    private static final Timer reminder = new Timer();

    public static void main(String[] args) throws LoginException {
        new DB();
        DB.createNewDatabase("bot");
        DB.createNewTable("users");

        JDA jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Listener())
                .addEventListeners(new UserManager())
                .addEventListeners(new BlackjackCommand())
                .setActivity(Activity.playing("New commands YAY!"))
                .build();

        jda.addEventListener(new CommandManager(jda));

        reminder.schedule(new TimerTask() {
            @Override
            public void run() {
                var now = LocalDateTime.now();
                var tag = now.getDayOfWeek().name();
                var stunde = now.getHour();
                var minute = now.getMinute();
                var seconds = now.getSecond();
                if (tag.equals("WEDNESDAY") && stunde == 18 && minute == 18 && seconds == 0) {
                    var channel = jda.getTextChannelById("755490778922352801");
                    if (channel != null) channel.sendMessage("Don't forget! Today we'll rol those math rocks!").mentionRoles("755490137118474270").queue();
                }
            }
        }, 5 * 1000, 1000);
    }

}