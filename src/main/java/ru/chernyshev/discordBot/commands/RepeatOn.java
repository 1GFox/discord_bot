package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chernyshev.discordBot.Annotations.BotCommand;
import ru.chernyshev.discordBot.services.NewPlayerManager;

import java.util.List;

@BotCommand(name = "repeat_song_on", description = "repeatOn")
public class RepeatOn implements ICommand {

    @Autowired
    private NewPlayerManager playerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        playerManager.repeatOn(event);
    }

    @Override
    public String getName() {
        return "repeat_song_on";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
