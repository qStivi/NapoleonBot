package qStivi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import qStivi.ICommand;
import qStivi.audioManagers.PlayerManager;
import qStivi.listeners.ControlsManager;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static qStivi.commands.JoinCommand.join;

public class DndCommand implements ICommand {

    static List<String> happyList = new ArrayList<>();
    static List<String> fightList = new ArrayList<>();
    static List<String> bossList = new ArrayList<>();
    static List<String> pirateList = new ArrayList<>();

    public DndCommand() {
        happyList.add("https://www.youtube.com/watch?v=owOV-TZI9ck"); // Sword Art Online Everyday Life
        happyList.add("https://www.youtube.com/watch?v=JliFaj-JHqw"); // Child of Light OST 09.Bolmus Populi
        happyList.add("https://www.youtube.com/watch?v=GWnWUNHN78Y"); // Sword Art Online OST - March Down
        happyList.add("https://www.youtube.com/watch?v=F_AARROfEno"); // Sword Art Online [AMV] - A Tender Feeling
        happyList.add("https://www.youtube.com/watch?v=0MCeu--w1JQ"); // Sword Art Online ~ A Tiny Love
        happyList.add("https://www.youtube.com/watch?v=XWnDuZLhKTI"); // Sword Art Online Original Soundtrack Vol 1 27 smile for me
        happyList.add("https://www.youtube.com/watch?v=gzdqmYrtOl0"); // Sword Art Online Original Soundtrack Vol 1 30 with my friend
        happyList.add("https://www.youtube.com/watch?v=x0q1u6YDoO0"); // Sword Art Online OST - The First Town
        happyList.add("https://www.youtube.com/watch?v=_pGaz_qN0cw"); // Lord of the Rings - Concerning Hobbits
        happyList.add("https://www.youtube.com/watch?v=l5hbBL9h1GI"); // Goblin Slayer OST - 04 - Written Request Notice
        happyList.add("https://www.youtube.com/watch?v=cL6IglpnI2Y"); // Goblin Slayer OST - 06 - Ushikai Musume's Morning
        happyList.add("https://www.youtube.com/watch?v=ywVhbtysZ9o"); // Goblin Slayer OST - 08 - The Days of Orcbolg
        happyList.add("https://www.youtube.com/watch?v=57pmN8ZSP0M"); // Goblin Slayer OST - 03 - Remoted Street
        happyList.add("https://www.youtube.com/watch?v=pzuWlTRmWJc"); // Goblin Slayer OST - 05 - Guild
        happyList.add("https://www.youtube.com/watch?v=-uHjKeWkZOM"); // Goblin Slayer OST - 07 - Meal with Comrade~Sweet!!
        happyList.add("https://youtu.be/xoRv-ygQs-s"); // Danmachi I OST - Meikyuu Toshi Orario
        happyList.add("https://youtu.be/zSao8Q7ngTE"); // Danmachi I OST - Yasashisa ni Tsutsumareta nara
        happyList.add("https://youtu.be/rgP4DgevQzs"); // Danmachi I OST - Eizokusuru Toki no Naka de
        happyList.add("https://youtu.be/Kk-uGAP_OIo"); // Danmachi II OST - Mayhem in the Tavern
        happyList.add("https://www.youtube.com/watch?v=DVhcpUoku_U"); // #02 Dawn Winery Theme｜Genshin Impact

        fightList.add("https://youtu.be/ZcNyPdipjeU"); // Forest Battle - Battle music
        fightList.add("https://www.youtube.com/watch?v=3bRN__YwhXU"); // Fantasy Battle Music - Goblin Raid
        fightList.add("https://www.youtube.com/watch?v=lW-npifVVcM"); // Sword Art Online Original Soundtrack Vol 1 24 he rules us
        fightList.add("https://www.youtube.com/watch?v=NDPftQZfh58"); // Child of Light OST Metal Gleamed in the Twilight [Full Choir Versions]
        fightList.add("https://www.youtube.com/watch?v=KsZAnfR4ON4"); // Epic Dark Battle Music - Escape [Powerful Fantasy Horror by Ebunny]
        fightList.add("https://www.youtube.com/watch?v=GmwzWYzgoRk"); // Child of Light OST 04.Jupiter's Lightning
        fightList.add("https://www.youtube.com/watch?v=Mp6uzqMNTeU"); // Sword Art Online OST - We Have To Defeat It
        fightList.add("https://www.youtube.com/watch?v=P244DsBEYxs"); // Star Wars - Battle of the Heroes Suite
        fightList.add("https://youtu.be/Fc9nTcROQtw"); // Two Steps From Hell - Norwegian pirate

        bossList.add("https://www.youtube.com/watch?v=xlYCxbBZUCY"); // John Williams - Duel of the Fates (Star Wars Soundtrack) [HQ]
        bossList.add("https://www.youtube.com/watch?v=KhPNuBi8pJM"); // World's Most Emotional Music | by Max Legend
        bossList.add("https://www.youtube.com/watch?v=TQUsnto_3pw"); // Shingeki no Kyojin - Attack on Titan Fight Theme
        bossList.add("https://youtu.be/XJoHG6sRKqU"); // The Place We Should Have Reached

        pirateList.add("https://youtu.be/7jW-JX3b6P0"); // The Sea of Thieves | Sea of Thieves OST
        pirateList.add("https://youtu.be/je09UmgAn2c"); // Maiden Voyage | Sea of Thieves OST
        pirateList.add("https://youtu.be/9dG0DVnB0do"); // Official Sea of Thieves Tavern Tunes: Summon the Megalodon
        pirateList.add("https://youtu.be/sRvBX4mVo8c"); // With Hammer and Hope | Sea of Thieves OST
        pirateList.add("https://youtu.be/1g1bDDBNtgc"); // Sea of Thieves Tall Tales Becalmed ( Shores of Gold Version ) Soundtrack Ost
        pirateList.add("https://www.youtube.com/watch?v=aCQd3dETk7Y"); // Grogg Mayles | 8-man Band! | EVERY Lead & Back-up Instrument
        pirateList.add("https://youtu.be/1YC2VVt5O_I"); // Bosun Bill | 8-man Band! | EVERY Lead & Back-up Instrument
        pirateList.add("https://youtu.be/AVlJagqF62w"); // Blackwake OST - Main Theme
        pirateList.add("https://youtu.be/UNREuwdJCw0"); // Assassin's Creed IV Black Flag - Assassin's Creed IV Black Flag Main Theme (Track 01)
        pirateList.add("https://youtu.be/F8IQdzJuhxA"); // Assassin's Creed IV Black Flag - I'll Be with You (Track 15)
    }

    @Override
    @Nonnull
    public CommandUpdateAction.@NotNull CommandData getCommand() {
        return new CommandUpdateAction.CommandData(getName(), getDescription())
                .addSubcommand(new CommandUpdateAction.SubcommandData("happy", "Happy/Traveling music")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "number", "Number of songs to play")
                                .setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("fight", "Fighting music")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "number", "number of songs tp play")
                                .setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("boss", "Boss music")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "number", "number of songs tp play")
                                .setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("pirate", "Casual pirate music")
                        .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "number", "number of songs tp play")
                                .setRequired(true)));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent event) {
        var hook = event.getHook();
        if (!join(event.getGuild(), event.getUser())) {
            hook.sendMessage("Please join a channel, so I can play your request.").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
            return;
        }

        PlayerManager.getINSTANCE().clearQueue(event.getGuild());

        for (int i = 0; i < event.getOption("number").getAsLong(); i++) {
            JoinCommand.join(event.getGuild(), event.getUser());
            PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), event.getGuild(), getSongByType(event.getSubcommandName()));
        }

        PlayerManager.getINSTANCE().skip(event.getGuild());

        hook.sendMessage("Playing D&D music.").delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();

        ControlsManager.getINSTANCE().sendMessage(event.getTextChannel(), event.getGuild());
    }

    private String getSongByType(String type) {
        if (type.equals("happy")) return happyList.get(ThreadLocalRandom.current().nextInt(happyList.size()));
        if (type.equals("fight")) return fightList.get(ThreadLocalRandom.current().nextInt(fightList.size()));
        if (type.equals("boss")) return bossList.get(ThreadLocalRandom.current().nextInt(bossList.size()));
        if (type.equals("pirate")) return pirateList.get(ThreadLocalRandom.current().nextInt(pirateList.size()));
        return null;
    }

    @Override
    public @Nonnull
    String getName() {
        return "dnd";
    }

    @Override
    public @Nonnull
    String getDescription() {
        return "Play background music for D&D";
    }
}
