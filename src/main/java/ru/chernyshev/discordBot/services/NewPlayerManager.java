package ru.chernyshev.discordBot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.yamusic.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class NewPlayerManager {
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private final HashMap<String, GuildQueueManager> guildsQueueManagers = new HashMap<>();


    public NewPlayerManager(CustomAuthYandexMusicUrlLoader customAuthYandexMusicUrlLoader, CustomAuthYandexMusicTrackLoader customAuthYandexMusicTrackLoader) {
        YandexMusicAudioSourceManager yandexMusicAudioSourceManager = new YandexMusicAudioSourceManager(
                true,
                customAuthYandexMusicTrackLoader,
                new DefaultYandexMusicPlaylistLoader(),
                customAuthYandexMusicUrlLoader,
                new DefaultYandexSearchProvider()
        );
        this.audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager(true, true, true));
        this.audioPlayerManager.registerSourceManager(yandexMusicAudioSourceManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public void createGuildsQueueManagers(List<Guild> guilds) {
        for (Guild guild : guilds) {
            guildsQueueManagers.put(guild.getId(), new GuildQueueManager(audioPlayerManager.createPlayer()));
        }
    }

    public void play(String songUrl, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        String guildId = guild.getId();
        Member member = event.getMember();
        assert member != null;
        AudioManager audioManager = guild.getAudioManager();

        GuildQueueManager guildQueueManager = guildsQueueManagers.get(guildId);

        AudioForwarder forwarder = new AudioForwarder(guildQueueManager.getPlayer(), guild);

        GuildVoiceState voiceState = member.getVoiceState();
        assert voiceState != null;
        audioManager.openAudioConnection(voiceState.getChannel());
        audioManager.setSendingHandler(forwarder);

        audioPlayerManager.loadItemOrdered(guildQueueManager, songUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildQueueManager.addTrack(audioTrack);
                if (guildQueueManager.isWasAddToQueue() && !guildQueueManager.isWasAddPlaylist()) {
                    event.reply(String.format("Трек %s добавлен в очередь", audioTrack.getInfo().title)).queue();
                    guildQueueManager.setWasAddToQueue(false);
                } else if (guildQueueManager.isWasAddFirst() && !guildQueueManager.isWasAddPlaylist()) {
                    event.reply("Сейчас играет: " + guildQueueManager.getPlayer().getPlayingTrack().getInfo().title).queue();
                    guildQueueManager.setWasAddFirst(false);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                guildQueueManager.setWasAddPlaylist(true);
                guildQueueManager.setPlaylist(audioPlaylist.getTracks());
                for (int i = 0; i < audioPlaylist.getTracks().size(); i++) {
                    guildQueueManager.addTrack(audioPlaylist.getTracks().get(i));
                }
                guildQueueManager.setWasAddPlaylist(false);
                event.reply(String.format("Треков в добавленном плейлисте: %s", guildQueueManager.getPlaylistTrackCounter())).queue();
                guildQueueManager.setPlaylistTrackCounter(0);
            }

            @Override
            public void noMatches() {
                event.reply("Ничего не нашел, братишка :(").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply("load failed").queue();
            }
        });
    }


    public void pause(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.getPlayer().setPaused(true);
        event.reply("Трек приостановлен").queue();
    }

    public void resume(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.getPlayer().setPaused(false);
        event.reply("Трек возобновлен").queue();
    }

    public void skip(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.getPlayer().stopTrack();
        event.reply("Трек пропущен").queue();
    }

    public void stop(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        if (guildQueueManager.isPlaylistRepeat() || guildQueueManager.isRepeatSong()) {
            guildQueueManager.setRepeatSong(false);
            guildQueueManager.setPlaylistRepeat(false);
        }
        guildQueueManager.clearQueue();
        guildQueueManager.getPlayer().stopTrack();
        event.reply("Все треки удалены из очереди").queue();
    }

    public void playlistRepeatOn(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.setPlaylistRepeat(true);
        event.reply("Повтор плейлиста включен").queue();
    }

    public void playlistRepeatOff(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.setPlaylistRepeat(false);
        event.reply("Повтор плейлиста выключен").queue();
    }

    public void repeatOn(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.setRepeatSong(true);
        event.reply("Повтор песни включен").queue();
    }

    public void repeatOff(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String id = event.getGuild().getId();
        GuildQueueManager guildQueueManager = guildsQueueManagers.get(id);
        guildQueueManager.setRepeatSong(false);
        event.reply("Повтор песни выключен").queue();
    }
}
