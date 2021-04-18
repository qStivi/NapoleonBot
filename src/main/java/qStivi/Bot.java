package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import qStivi.commands.BlackjackCommand;
import qStivi.db.DB;
import qStivi.listeners.ControlsManager;
import qStivi.listeners.Listener;
import qStivi.listeners.UserManager;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class Bot {
    private static final Timer reminder = new Timer();
    private static final Timer activityUpdate = new Timer();
    private static final String ACTIVITY = "Evolving...";
    private static final Logger logger = getLogger(BlackjackCommand.class);

    public static void main(String[] args) throws LoginException, InterruptedException {
        logger.info("Booting...");
//        TimeUnit.MINUTES.sleep(1);
        new DB();
        DB.createNewDatabase("bot");
        DB.createNewUsersTable("users");
        DB.createNewQuotasTable();

        JDA jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Listener())
                .addEventListeners(new UserManager())
                .addEventListeners(new BlackjackCommand())
                .setActivity(getActivity())
                .build();

        jda.addEventListener(new CommandManager(jda));

        activityUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                jda.getPresence().setActivity(getActivity());
            }
        },60*1000,60*1000);



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

    private static Activity getActivity() {
        List<Activity> activities = new ArrayList<>();
        activities.add(Activity.competing(ACTIVITY));
        activities.add(Activity.playing(ACTIVITY));
        activities.add(Activity.listening(ACTIVITY));
        activities.add(Activity.listening(ACTIVITY));
        activities.add(Activity.watching(ACTIVITY));
        Collections.shuffle(activities);

        return activities.get(0);
    }

}