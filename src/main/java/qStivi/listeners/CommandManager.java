package qStivi.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qStivi.ICommand;
import qStivi.commands.*;
import qStivi.db.DB;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandManager extends ListenerAdapter {
    private static final Logger logger = getLogger(CommandManager.class);

    public final List<ICommand> commandList = new ArrayList<>();
    public final Queue<SlashCommandEvent> events = new LinkedList<>();

    public CommandManager(JDA jda) {
        commandList.add(new TestCommand());
        commandList.add(new RollCommand());
        commandList.add(new StopCommand());
        commandList.add(new ContinueCommand());
        commandList.add(new DndCommand());
        commandList.add(new PauseCommand());
        commandList.add(new RepeatCommand());
        commandList.add(new SkipCommand());
        commandList.add(new CleanCommand());
        commandList.add(new JoinCommand());
        commandList.add(new LeaveCommand());
        commandList.add(new PingCommand());
        commandList.add(new RedditCommand());
        commandList.add(new PlayCommand());
        commandList.add(new ShutdownCommand());
        commandList.add(new StatsCommand());
        commandList.add(new Top10Command());
        commandList.add(new WorkCommand());
        commandList.add(new BlackjackCommand());
        commandList.add(new moneyCommand());


        List<CommandUpdateAction.CommandData> commandDataList = new ArrayList<>();
        for (ICommand command : commandList) {
            commandDataList.add(command.getCommand());
        }
        jda.updateCommands().addCommands(commandDataList).queue();


    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        try {
            event.acknowledge().queue();
        } catch (Exception e){
            try {
                event.acknowledge().queue();
            } catch (Exception ignored){}
        }
//        if (!(event.getUser().getIdLong() == 219108246143631364L)) return;
        for (ICommand command : commandList) {
            if (command.getCommand().getName().equals(event.getName())) {

                var db = new DB();
                logger.info(event.getUser().getName() + " issued /" + event.getName());
                db.update("users", "last_command", "id", event.getUser().getIdLong(), new Date().getTime() / 1000);

//                events.offer(event);
                command.handle(event);
                logger.info("Event offered.");
            }
        }
    }
}
