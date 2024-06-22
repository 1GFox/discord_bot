package ru.chernyshev.discordBot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.List;

public class GuildQueueManager extends AudioEventAdapter {
    private final TrackQueue trackQueue;
    private boolean repeatSong = false;
    private boolean wasAddToQueue = false;
    private boolean wasAddFirst = false;
    private boolean wasAddPlaylist = false;
    private int playlistTrackCounter = 0;
    private List<AudioTrack> playlist;
    private boolean playlistRepeat = false;
    private final AudioPlayer player;


    public GuildQueueManager(AudioPlayer audioPlayer) {
        this.player = audioPlayer;
        this.trackQueue = new TrackQueue(audioPlayer, this);
    }

    public void addTrack(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            trackQueue.addTrack(track);
            if (wasAddPlaylist) {
                playlistTrackCounter++;
            } else {
                wasAddToQueue = true;
            }
        } else if (wasAddPlaylist) {
            playlistTrackCounter++;
        } else {
            wasAddFirst = true;
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (repeatSong) {
            player.startTrack(track.makeClone(), false);
            return;
        }
        if (playlistRepeat && trackQueue.isEmpty()) {
            for (AudioTrack track1 : playlist) {
                trackQueue.addTrack(track1.makeClone());
            }
        }
        trackQueue.playNextTrack();
    }

    public void setRepeatSong(boolean repeatSong) {
        this.repeatSong = repeatSong;
    }

    public void setWasAddToQueue(boolean wasAddToQueue) {
        this.wasAddToQueue = wasAddToQueue;
    }

    public void setWasAddFirst(boolean wasAddFirst) {
        this.wasAddFirst = wasAddFirst;
    }

    public void setWasAddPlaylist(boolean wasAddPlaylist) {
        this.wasAddPlaylist = wasAddPlaylist;
    }

    public void setPlaylistTrackCounter(int playlistTrackCounter) {
        this.playlistTrackCounter = playlistTrackCounter;
    }

    public void setPlaylist(List<AudioTrack> playlist) {
        this.playlist = playlist;
    }

    public void setPlaylistRepeat(boolean playlistRepeat) {
        this.playlistRepeat = playlistRepeat;
    }

    public boolean isRepeatSong() {
        return repeatSong;
    }

    public boolean isWasAddToQueue() {
        return wasAddToQueue;
    }

    public boolean isWasAddFirst() {
        return wasAddFirst;
    }

    public boolean isWasAddPlaylist() {
        return wasAddPlaylist;
    }

    public int getPlaylistTrackCounter() {
        return playlistTrackCounter;
    }

    public List<AudioTrack> getPlaylist() {
        return playlist;
    }

    public boolean isPlaylistRepeat() {
        return playlistRepeat;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public void clearQueue() {
      this.trackQueue.clear();
    }

}
