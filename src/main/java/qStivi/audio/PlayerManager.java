package qStivi.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getINSTANCE() {

        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackURL) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);

                AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();

                MessageEmbed msg = new EmbedBuilder()
                        .setColor(Color.red)
                        .setAuthor(playingTrack.getInfo().author)
                        .setTitle(playingTrack.getInfo().title)
                        .setDescription(playingTrack.getPosition() + " | " + "o---------------------------" + " | " + playingTrack.getDuration())
                        .addField("1", "o-------------------------------", false)
                        .addField("2", "o-----------------------------------", false)
                        .addField("3", "o---------------------------------------", false)
                        .addField("4", "o-------------------------------------------", false)
                        .addField("5", "o-----------------------------------------------", false)
                        .addField("6", "o---------------------------------------------------", false)
                        .addField("7", "o-------------------------------------------------------", false)
                        .addField("8", "o-----------------------------------------------------------", false)
                        .addField("9", "o---------------------------------------------------------------", false)
                        .addField("10", "o-------------------------------------------------------------------", false)
                        .build();

                channel.sendMessage(msg).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public void skip(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.trackScheduler.playNextTrack();
    }

    public void pause(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.trackScheduler.pauseTrack();
    }

    public void continueTrack(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        musicManager.trackScheduler.continueTrack();
    }
}
