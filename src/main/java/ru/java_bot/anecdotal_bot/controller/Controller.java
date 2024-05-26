package ru.java_bot.anecdotal_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java_bot.anecdotal_bot.model.JokeModel;
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
    ResponseEntity<List<JokeModel>> getAllJokes(){
        return ResponseEntity.ok(jokeService.getAllJokes());
    }

    @GetMapping("/{id}")
    ResponseEntity<JokeModel> getJokeById(@PathVariable Long id){
        return jokeService.getJokeById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
}
