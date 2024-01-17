package ru.hoiboi.gfdocsbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class GfDocsBotApplication {
	public static void main(String[] args) throws TelegramApiException {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(new TelegramDocsBot());
		SpringApplication.run(GfDocsBotApplication.class, args);
	}
}
