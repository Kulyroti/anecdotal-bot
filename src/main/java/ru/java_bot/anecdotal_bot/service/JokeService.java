package ru.java_bot.anecdotal_bot.service;

import org.springframework.data.domain.Page;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.model.JokeWithCount;

import java.util.List;
import java.util.Optional;

public interface JokeService {
    Optional<JokeModel> createJoke(JokeModel text);

    Page<JokeModel> getAllJokes(int page);

    Optional<JokeModel> getJokeById(Long id, Long userId);//

    void deleteJokeById(Long id);//

    Optional<JokeModel> changeJokeById(Long id, JokeModel text);///

    List<JokeWithCount> getTop5Jokes();

    Optional<JokeModel> getRandomJoke();
}
