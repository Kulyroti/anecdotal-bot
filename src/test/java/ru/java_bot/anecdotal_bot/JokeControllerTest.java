package ru.java_bot.anecdotal_bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.java_bot.anecdotal_bot.controller.Controller;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.service.JokeService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller.class)
class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeService jokeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createJoke() throws Exception {
        JokeModel joke = new JokeModel(null, "Новый анекдот", null, null);
        mockMvc.perform(MockMvcRequestBuilders.post("/jokes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joke)))
                .andExpect(status().isOk());

        verify(jokeService, times(1)).createJoke(joke);
    }


    @Test
    void getJokeById() throws Exception {
        JokeModel joke = new JokeModel(1L, "Какой-то анекдот", null, null);
        when(jokeService.getJokeById(1L, 2L)).thenReturn(Optional.of(joke));

        mockMvc.perform(MockMvcRequestBuilders.get("/jokes/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(joke)));
    }

    @Test
    void getJokeByIdNotFound() throws Exception {
        when(jokeService.getJokeById(2L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/jokes/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeJoke() throws Exception {
        JokeModel joke = new JokeModel(1L, "Изменённый анекдот", null, null);
        when(jokeService.changeJokeById(1L, joke)).thenReturn(Optional.of(joke));

        mockMvc.perform(MockMvcRequestBuilders.put("/jokes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joke)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(joke)));
    }

    @Test
    void changeJokeNotFound() throws Exception {
        JokeModel joke = new JokeModel(2L, "Изменённый анекдот", null, null);
        when(jokeService.changeJokeById(2L, joke)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/jokes/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joke)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteJoke() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/jokes/1"))
                .andExpect(status().isOk());

        verify(jokeService, times(1)).deleteJokeById(1L);
    }
}