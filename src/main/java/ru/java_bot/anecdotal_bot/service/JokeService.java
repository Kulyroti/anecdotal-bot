package ru.java_bot.anecdotal_bot.service;

import ru.java_bot.anecdotal_bot.model.JokeModel;

import java.util.List;
import java.util.Optional;

public interface JokeService {
    Optional<JokeModel> createJoke(JokeModel text);

    List<JokeModel> getAllJokes();//

    Optional<JokeModel> getJokeById(Long id);//

    void deleteJokeById(Long id);//

    Optional<JokeModel> changeJokeById(Long id, JokeModel text);///
}
