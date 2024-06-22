package ru.chernyshev.discordBot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.chernyshev.discordBot.services.Listener;

import java.util.List;

@Configuration
@ComponentScan({"com.sedmelluq.discord.lavaplayer.source.yamusic"})
public class AppConfig {

    @Value("${discord.token}")
    private String discordToken;



    @Bean
    public JDA jda(Listener listener) {

        JDA jda = JDABuilder.createDefault(discordToken)
                .enableIntents(
                        List.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_VOICE_STATES
                        )
                )
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("how Anton becomes a programmer"))
                .build();
        jda.addEventListener(listener);

        return jda;
    }


}
