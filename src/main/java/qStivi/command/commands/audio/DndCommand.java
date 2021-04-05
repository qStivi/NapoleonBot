package qStivi.command.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import qStivi.audioManagers.PlayerManager;
import qStivi.command.CommandContext;
import qStivi.command.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static qStivi.command.commands.JoinCommand.join;

public class DndCommand implements ICommand {
    @Override
    public void handle(CommandContext context) {
        if (context.getArgs().size() == 1) {
            if (context.getArgs().get(0).equalsIgnoreCase("happy")) {
                play(context, getHappy());
            }
            if (context.getArgs().get(0).equalsIgnoreCase("fight")) {
                play(context, getFight());
            }
        } else if (context.getArgs().size() > 1) {
            if (context.getArgs().get(0).equalsIgnoreCase("happy")) {
                for (int i = 0; i < Integer.parseInt(context.getArgs().get(1)); i++) {
                    play(context, getHappy());
                }
            }
            if (context.getArgs().get(0).equalsIgnoreCase("fight")) {
                for (int i = 0; i < Integer.parseInt(context.getArgs().get(1)); i++) {
                    play(context, getFight());
                }
            }
        }
    }

    private void play(CommandContext context, String link) {
        Guild guild = context.getGuild();
        User author = context.getAuthor();
        context.getGuild().getAudioManager();
        if (!context.getGuild().getAudioManager().isConnected()) join(guild, author);
        PlayerManager.getINSTANCE().loadAndPlay(context.getGuild(), link);
    }

    private String getHappy() {
        List<String> list = new ArrayList<>();
        list.add("https://www.youtube.com/watch?v=owOV-TZI9ck"); // Sword Art Online Everyday Life
        list.add("https://www.youtube.com/watch?v=JliFaj-JHqw"); // Child of Light OST 09.Bolmus Populi
        list.add("https://www.youtube.com/watch?v=GWnWUNHN78Y"); // Sword Art Online OST - March Down
        list.add("https://www.youtube.com/watch?v=F_AARROfEno"); // Sword Art Online [AMV] - A Tender Feeling
        list.add("https://www.youtube.com/watch?v=0MCeu--w1JQ"); // Sword Art Online ~ A Tiny Love
        list.add("https://www.youtube.com/watch?v=XWnDuZLhKTI"); // Sword Art Online Original Soundtrack Vol 1 27 smile for me
        list.add("https://www.youtube.com/watch?v=gzdqmYrtOl0"); // Sword Art Online Original Soundtrack Vol 1 30 with my friend
        list.add("https://www.youtube.com/watch?v=x0q1u6YDoO0"); // Sword Art Online OST - The First Town
        list.add("https://www.youtube.com/watch?v=_pGaz_qN0cw"); // Lord of the Rings - Concerning Hobbits
        list.add("https://www.youtube.com/watch?v=l5hbBL9h1GI"); // Goblin Slayer OST - 04 - Written Request Notice
        list.add("https://www.youtube.com/watch?v=cL6IglpnI2Y"); // Goblin Slayer OST - 06 - Ushikai Musume's Morning
        list.add("https://www.youtube.com/watch?v=ywVhbtysZ9o"); // Goblin Slayer OST - 08 - The Days of Orcbolg
        list.add("https://www.youtube.com/watch?v=57pmN8ZSP0M"); // Goblin Slayer OST - 03 - Remoted Street
        list.add("https://www.youtube.com/watch?v=pzuWlTRmWJc"); // Goblin Slayer OST - 05 - Guild
        list.add("https://www.youtube.com/watch?v=-uHjKeWkZOM"); // Goblin Slayer OST - 07 - Meal with Comrade~Sweet!!

        int r = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(r);
    }

    private String getFight() {
        List<String> list = new ArrayList<>();
        list.add("https://youtu.be/ZcNyPdipjeU"); // Forest Battle - Battle music
        list.add("https://www.youtube.com/watch?v=3bRN__YwhXU"); // Fantasy Battle Music - Goblin Raid
        list.add("https://www.youtube.com/watch?v=lW-npifVVcM"); // Sword Art Online Original Soundtrack Vol 1 24 he rules us
        list.add("https://www.youtube.com/watch?v=NDPftQZfh58"); // Child of Light OST Metal Gleamed in the Twilight [Full Choir Versions]
        list.add("https://www.youtube.com/watch?v=KsZAnfR4ON4"); // Epic Dark Battle Music - Escape [Powerful Fantasy Horror by Ebunny]
        list.add("https://www.youtube.com/watch?v=GmwzWYzgoRk"); // Child of Light OST 04.Jupiter's Lightning
        list.add("https://www.youtube.com/watch?v=Mp6uzqMNTeU"); // Sword Art Online OST - We Have To Defeat It
        list.add("https://www.youtube.com/watch?v=TQUsnto_3pw"); // Shingeki no Kyojin - Attack on Titan Fight Theme
        list.add("https://www.youtube.com/watch?v=xlYCxbBZUCY"); // John Williams - Duel of the Fates (Star Wars Soundtrack) [HQ]
        list.add("https://www.youtube.com/watch?v=P244DsBEYxs"); // Star Wars - Battle of the Heroes Suite

        int r = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(r);
    }

    @Override
    public String getName() {
        return "dnd";
    }

    @Override
    public String getHelp() {
        return "Plays random dnd music";
    }
}
