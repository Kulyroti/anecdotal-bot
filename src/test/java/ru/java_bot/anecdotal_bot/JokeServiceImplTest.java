package ru.java_bot.anecdotal_bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.repository.JokeRepository;
import ru.java_bot.anecdotal_bot.service.JokeService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JokeServiceImplTest {

    @Mock
    private JokeRepository jokesJokeRepository;

    @InjectMocks
    private JokeService jokeService;

    @Test
    void createJoke() {
        JokeModel joke = new JokeModel(null, "Новый анекдот", null, null);
        when(jokesJokeRepository.save(joke)).thenReturn(joke);

        Optional<JokeModel> result = jokeService.createJoke(joke);

        assertTrue(result.isPresent());
        assertEquals(result.get(), joke);
        verify(jokesJokeRepository, times(1)).save(joke);
    }

    @Test
    void getJokeByIdNotFound() {
        when(jokesJokeRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<JokeModel> result = jokeService.getJokeById(2L, 3L);

        assertFalse(result.isPresent());
        verify(jokesJokeRepository, times(1)).findById(2L);
    }

    @Test
    void getJokeById() {
        JokeModel joke = new JokeModel(1L, "Анекдот 3", null, null);
        when(jokesJokeRepository.findById(1L)).thenReturn(Optional.of(joke));

        Optional<JokeModel> result = jokeService.getJokeById(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(result.get(), joke);
        verify(jokesJokeRepository, times(1)).findById(1L);
    }

    @Test
    void deleteJokeById() {
        jokeService.deleteJokeById(1L);

        verify(jokesJokeRepository, times(1)).deleteById(1L);
    }

    @Test
    void changeJokeById() {
        JokeModel existingJoke = new JokeModel(1L, "Старый анекдот", null, null);
        JokeModel updatedJoke = new JokeModel(1L, "Изменённый анекдот", null, null);
        when(jokesJokeRepository.findById(1L)).thenReturn(Optional.of(existingJoke));
        when(jokesJokeRepository.save(existingJoke)).thenReturn(existingJoke);

        Optional<JokeModel> result = jokeService.changeJokeById(1L, updatedJoke);

        assertTrue(result.isPresent());
        assertEquals(result.get(), existingJoke);
        assertEquals(result.get().getText(), updatedJoke.getText());
        verify(jokesJokeRepository, times(1)).findById(1L);
        verify(jokesJokeRepository, times(1)).save(existingJoke);
    }

    @Test
    void changeJokeByIdNotFound() {
        when(jokesJokeRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<JokeModel> result = jokeService.changeJokeById(2L, new JokeModel(2L, "Новый анекдот", null, null));

        assertFalse(result.isPresent());
        verify(jokesJokeRepository, times(1)).findById(2L);
        verify(jokesJokeRepository, never()).save(any());
    }
}
