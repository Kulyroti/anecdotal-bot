package ru.java_bot.anecdotal_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class JokeServiceImpl implements JokeService {
    private final Repository jokesRepository;

    @Override
    public Optional<JokeModel> createJoke(JokeModel text){
        return Optional.of(jokesRepository.save(text));
    }

    @Override
    public List<JokeModel> getAllJokes(){
        return jokesRepository.findAll();
    }

    @Override
    public Optional<JokeModel> getJokeById(Long id) {
        return jokesRepository.findById(id);
    }

    @Override
    public void deleteJokeById(Long id) {
        jokesRepository.deleteById(id);
    }

    @Override
    public Optional<JokeModel> changeJokeById(Long id, JokeModel text) {
        Optional<JokeModel> jokeOptional = jokesRepository.findById(id);
        jokeOptional.ifPresent(joke -> {
            joke.setText(text.getText());
            joke.setUpdatedDate(LocalDateTime.now());
            jokesRepository.save(joke);
        });
        return jokeOptional;
    }
}
