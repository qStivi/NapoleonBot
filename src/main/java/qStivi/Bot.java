package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.slf4j.Logger;
import qStivi.commands.BlackjackCommand;
import qStivi.db.DB;
import qStivi.listeners.CommandManager;
import qStivi.listeners.ControlsManager;
import qStivi.listeners.Listener;
import qStivi.listeners.UserManager;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class Bot {
    private static final Timer reminder = new Timer();
    private static final Timer activityUpdate = new Timer();
    private static final String ACTIVITY = "Evolving...";
    private static final Logger logger = getLogger(Bot.class);
    private static final Timer timer = new Timer();
    private static final Timer timer2 = new Timer();

    public static void main(String[] args) throws LoginException {
        logger.info("Booting...");
        var db = new DB();
        db.createNewDatabase("bot");
        db.createNewTable("users",
                "money integer default 1000," +
                        "xp integer default 0," +
                        "last_worked integer default 0," +
                        "last_chat_message integer default 0," +
                        "last_command integer default 0," +
                        "last_reaction integer default 0," +
                        "command_times_blackjack integer default 0," +
                        "xp_reaction integer default 0," +
                        "xp_voice integer default 0," +
                        "xp_chat integer default 0," +
                        "blackjack_wins integer default 0," +
                        "blackjack_loses integer default 0," +
                        "blackjack_draws integer default 0"
        );

        var jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Listener())
                .addEventListeners(new UserManager())
                .addEventListeners(new BlackjackCommand())
                .setActivity(getActivity())
                .build();

        var cm = new CommandManager(jda);
        jda.addEventListener(cm);

        activityUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                jda.getPresence().setActivity(getActivity());
            }
        }, 10 * 1000, 10 * 1000);


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

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (cm.events.isEmpty()) return;
                var event = cm.events.poll();
                for (ICommand command : cm.commandList) {
                    if (command.getCommand().getName().equals(event.getName())) {
                        command.handle(event);
                        logger.info("Event handled.");
                    }
                }

            }
        }, 10*1000, 3*1000);
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