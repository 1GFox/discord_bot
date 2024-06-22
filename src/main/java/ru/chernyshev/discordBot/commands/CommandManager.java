package ru.chernyshev.discordBot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.springframework.stereotype.Service;
import ru.chernyshev.discordBot.Annotations.BotCommand;

import java.util.HashMap;
import java.util.List;

@Service
public class CommandManager {
    private final HashMap<String, ICommand> commands = new HashMap<>();


    public void registerCommand(ICommand iCommand, ReadyEvent event) {
        BotCommand annotation = iCommand.getClass().getAnnotation(BotCommand.class);
        String name = annotation.name();
        String description = annotation.description();

        List<Guild> guilds = event.getJDA().getGuilds();
        for (Guild guild : guilds) {
            guild.upsertCommand(name, description).addOptions(iCommand.getOptions()).queue();
        }

        commands.put(name, iCommand);
    }

    public void runCommand(SlashCommandInteractionEvent event){
        ICommand command = commands.get(event.getName());
        command.execute(event);
    }
}
