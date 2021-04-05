package qStivi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import qStivi.command.commands.audio.ControlsManager;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new Listener())
                .addEventListeners(new ControlsManager())
                .addEventListeners(new Bot())
                .setActivity(Activity.listening("/help for more info..."))
                .build();

        var testCommand = new CommandUpdateAction.CommandData("test", "This command is to test the new slash command system :)")
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING, "text", "What should the bot reply.")
                        .setRequired(true));

        var pingCommand = new CommandUpdateAction.CommandData("ping", "This is PingPong!")
                .addOption(new CommandUpdateAction.SubcommandGroupData("group", "description")
                        .addSubcommand(new CommandUpdateAction.SubcommandData("subcommand", "description")));

        jda.updateCommands().addCommands(testCommand, pingCommand).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        switch (event.getName()) {
            case "test":
                event.reply(Objects.requireNonNull(event.getOption("text")).getAsString()).queue();
                break;
            case "ping":
                event.reply("Pong!").setEphemeral(true).queue();
            default:
                event.reply("Nope!").queue();
        }
    }

}