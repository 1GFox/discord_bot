package ru.chernyshev.discordBot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackQueue {
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private final AudioPlayer player;


    public TrackQueue(AudioPlayer player, GuildQueueManager guildQueueManager) {
        this.player = player;
        player.addListener(guildQueueManager);
    }

    public void addTrack(AudioTrack track) {
        this.queue.offer(track);
    }

    public void playNextTrack() {
        player.startTrack(queue.poll(), false);
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void clear() {
        this.queue.clear();
    }
}
