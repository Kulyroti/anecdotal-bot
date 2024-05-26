package ru.java_bot.anecdotal_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.model.JokeWithCount;
import ru.java_bot.anecdotal_bot.service.JokeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jokes")
@RequiredArgsConstructor
public class Controller {

    private final JokeService jokeService;

    @PostMapping
    ResponseEntity<Void> createJoke(@RequestBody JokeModel text){
        jokeService.createJoke(text);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<JokeModel>> getAllJokes(@RequestParam int page){
        return ResponseEntity.ok(jokeService.getAllJokes(page));
    }

    @GetMapping("/{id}")
    ResponseEntity<JokeModel> getJokeById(@PathVariable Long id, @RequestParam("userId") Long userId){
        return jokeService.getJokeById(id, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<JokeModel> changeJoke(@PathVariable Long id, @RequestBody JokeModel textJoke){
        Optional<JokeModel> changedJoke = jokeService.changeJokeById(id, textJoke);
        return changedJoke.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteJoke(@PathVariable Long id){
        jokeService.deleteJokeById(id);
        return ResponseEntity.ok().build();
    }

    //топ 5 популярных шуток
    @GetMapping("/top5")
    ResponseEntity<List<JokeWithCount>> getTop5Jokes() {
        return ResponseEntity.ok(jokeService.getTop5Jokes());
    }

    //рандомная шутка
    @GetMapping("/random")
    public ResponseEntity<JokeModel> getRandomJoke() {
        return jokeService.getRandomJoke()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
