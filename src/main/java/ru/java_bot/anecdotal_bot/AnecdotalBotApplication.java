package ru.java_bot.anecdotal_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableAspectJAutoProxy
@SpringBootApplication
@EnableTransactionManagement
public class AnecdotalBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnecdotalBotApplication.class, args);
	}

}
