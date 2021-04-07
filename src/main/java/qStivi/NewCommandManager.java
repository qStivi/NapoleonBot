package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NewCommandManager extends ListenerAdapter {

    private final List<INewCommand> commandList = new ArrayList<>();

    public NewCommandManager(JDA jda) {
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


        List<CommandUpdateAction.CommandData> commandDataList = new ArrayList<>();
        for (INewCommand command : commandList) {
            commandDataList.add(command.getCommand());
        }
        jda.updateCommands().addCommands(commandDataList).queue();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        for (INewCommand command : commandList) {
            if (command.getCommand().getName().equals(event.getName())) {
                command.handle(event);
            }
        }
    }
}
