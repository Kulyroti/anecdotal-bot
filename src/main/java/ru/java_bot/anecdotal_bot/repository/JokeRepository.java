package ru.java_bot.anecdotal_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.java_bot.anecdotal_bot.model.JokeModel;

import java.util.Optional;

public interface JokeRepository extends PagingAndSortingRepository<JokeModel, Long>, JpaRepository<JokeModel, Long> {
    @Query("SELECT j FROM JokeModel j ORDER BY RANDOM() LIMIT 1")
    Optional<JokeModel> findRandomJoke();
}
