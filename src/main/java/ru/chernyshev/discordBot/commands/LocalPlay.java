package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chernyshev.discordBot.Annotations.BotCommand;
import ru.chernyshev.discordBot.services.NewPlayerManager;

import java.util.List;

@BotCommand(name = "localplay", description = "play_local_music")
public class LocalPlay implements ICommand {
    private final String name = "localplay";


    @Autowired
    private NewPlayerManager playerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            String requiredURL = "C:\\music\\" + event.getOption("name").getAsString() + ".mp3";
            playerManager.play(requiredURL, event);
        } catch (IllegalArgumentException e) {
            event.reply("Вы должны находиться в голосовом канале").queue();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "name", "name of the song", true));
    }
}
