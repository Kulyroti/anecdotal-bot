package ru.java_bot.anecdotal_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java_bot.anecdotal_bot.model.JokeModel;

public interface Repository extends JpaRepository<JokeModel, Long> {

}
