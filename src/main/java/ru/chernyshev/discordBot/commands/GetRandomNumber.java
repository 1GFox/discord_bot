package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.chernyshev.discordBot.Annotations.BotCommand;

import java.util.List;
import java.util.Random;

@BotCommand(name = "roll_the_dice", description = "random number")
public class GetRandomNumber implements ICommand {


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Random random = new Random();
        int y = event.getOption("d").getAsInt();

        int result = random.nextInt(y);
        event.reply(String.valueOf(result + 1)).queue();
    }

    @Override
    public String getName() {
        return "roll_the_dice";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "d", "количество граней", true));
    }
}
