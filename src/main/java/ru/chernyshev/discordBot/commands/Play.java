package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chernyshev.discordBot.Annotations.BotCommand;
import ru.chernyshev.discordBot.services.NewPlayerManager;

import java.util.List;
import java.util.Objects;

@BotCommand(name = "play", description = "play_music")
public class Play implements ICommand {
    private final String name = "play";

    @Autowired
    private NewPlayerManager playerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            playerManager.play(Objects.requireNonNull(event.getOption("url")).getAsString(), event);
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
        return List.of(new OptionData(OptionType.STRING, "url", "url of the song", true));
    }
}
