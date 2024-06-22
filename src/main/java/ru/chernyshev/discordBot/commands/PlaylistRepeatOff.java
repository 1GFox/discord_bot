package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chernyshev.discordBot.Annotations.BotCommand;
import ru.chernyshev.discordBot.services.NewPlayerManager;

import java.util.List;

@BotCommand(name = "repeat_playlist_off", description = "stop repeating current playlist")
public class PlaylistRepeatOff implements ICommand {

    @Autowired
    private NewPlayerManager playerManager;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        playerManager.playlistRepeatOff(event);
    }

    @Override
    public String getName() {
        return "repeat_playlist_off";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
