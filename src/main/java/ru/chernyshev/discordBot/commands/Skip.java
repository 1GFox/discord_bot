package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chernyshev.discordBot.Annotations.BotCommand;
import ru.chernyshev.discordBot.services.NewPlayerManager;

import java.util.List;

@BotCommand(name = "skip", description = "skipping_current_track")
public class Skip implements ICommand {

    @Autowired
    private NewPlayerManager playerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        playerManager.skip(event);
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
