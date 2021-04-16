package qStivi.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.db.DB;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.slf4j.LoggerFactory.getLogger;

public class UserManager extends ListenerAdapter {
    private static final Logger logger = getLogger(DB.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    DB db = new DB();
    Timer timer = new Timer();
    List<Task> tasks = new ArrayList<>();

    public UserManager() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Task> _tasks;

                Lock r = lock.readLock();
                r.lock();
                try {
                    _tasks = tasks;
                } finally {
                    r.unlock();
                }

                for (Task task : _tasks) {
                    task.run();
                }
            }
        }, 60 * 1000, 60 * 1000);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
        var id = Long.parseLong(event.getAuthor().getId());
        if (!db.userExists(id)) {
            db.insert(id);
        }

        var xp = db.getXp(id);
        db.setXp(id, xp + 10);
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        var id = Long.parseLong(event.getUser().getId());
        if (!db.userExists(id)) {
            db.insert(id);
        }

        var xp = db.getXp(id);
        db.setXp(id, xp + 5);
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if (event.getMember().getUser().isBot()) return;

        var id = Long.parseLong(event.getMember().getUser().getId());
        if (!db.userExists(id)) {
            db.insert(id);
        }

        Task task = new Task(new TimerTask() {
            @Override
            public void run() {
                var xp = db.getXp(id);
                db.setXp(id, xp + 1);
            }
        }, id);

        Lock w = lock.writeLock();
        w.lock();
        try {
            tasks.add(task);
        } finally {
            w.unlock();
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        var id = Long.parseLong(event.getMember().getId());
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.id == id) {
                Lock w = lock.writeLock();
                w.lock();
                try {
                    tasks.remove(task);
                } finally {
                    w.unlock();
                }
                task.cancel();
            }
        }
    }

    public static class Task {
        TimerTask timerTask;
        Long id;

        public Task(TimerTask timerTask, Long id) {
            this.timerTask = timerTask;
            this.id = id;
        }

        private void run() {
            this.timerTask.run();
        }

        private void cancel() {
            this.timerTask.cancel();
        }
    }
}
