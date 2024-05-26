package ru.java_bot.anecdotal_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.java_bot.anecdotal_bot.model.JokeCalls;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.model.JokeWithCount;
import ru.java_bot.anecdotal_bot.repository.CallsRepository;
import ru.java_bot.anecdotal_bot.repository.JokeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JokeServiceImpl implements JokeService {
    private final JokeRepository jokesJokeRepository;
    private final CallsRepository callsRepository;

    @Override
    public Optional<JokeModel> createJoke(JokeModel text){
        return Optional.of(jokesJokeRepository.save(text));
    }

    @Override
    public Page<JokeModel> getAllJokes(int page){
        int size = 2;
        return jokesJokeRepository.findAll(PageRequest.of(page, size));
    }


    @Override
    public Optional<JokeModel> getJokeById(Long id, Long userId) {
        Optional<JokeModel> jokeOptional = jokesJokeRepository.findById(id);

        if (jokeOptional.isPresent()) {
            // Шутка найдена, создаем запись в JokeCalls
            JokeCalls call = new JokeCalls();
            call.setJoke(jokeOptional.get());
            call.setUserId(userId);
            callsRepository.save(call);
        }

        return jokeOptional;
    }

    @Override
    public void deleteJokeById(Long id) {
        jokesJokeRepository.deleteById(id);
    }

    @Override
    public Optional<JokeModel> changeJokeById(Long id, JokeModel text) {
        Optional<JokeModel> jokeOptional = jokesJokeRepository.findById(id);
        jokeOptional.ifPresent(joke -> {
            joke.setText(text.getText());
            joke.setUpdatedDate(LocalDateTime.now());
            jokesJokeRepository.save(joke);
        });
        return jokeOptional;
    }


    @Override
    public List<JokeWithCount> getTop5Jokes() {
        return callsRepository.findTop5JokesByCalls();

    }

    @Override
    public Optional<JokeModel> getRandomJoke() {
        return jokesJokeRepository.findRandomJoke();
    }
}
