package ru.chernyshev.discordBot.services;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.chernyshev.discordBot.commands.CommandManager;
import ru.chernyshev.discordBot.commands.ICommand;

import java.util.List;

@Component
public class Listener extends ListenerAdapter {

    @Autowired
    private CommandManager commandManager;
    @Autowired
    private List<ICommand> allCommands;
    @Autowired
    private NewPlayerManager newPlayerManager;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        newPlayerManager.createGuildsQueueManagers(event.getJDA().getGuilds());
        for (ICommand command : allCommands) {
            commandManager.registerCommand(command, event);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandManager.runCommand(event);
    }
}
