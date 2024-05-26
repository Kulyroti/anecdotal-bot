package ru.java_bot.anecdotal_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java_bot.anecdotal_bot.model.JokeCalls;
import ru.java_bot.anecdotal_bot.model.JokeWithCount;

import java.util.List;

public interface CallsRepository extends JpaRepository<JokeCalls, Long> {
    @Query("SELECT new ru.java_bot.anecdotal_bot.model.JokeWithCount(j.joke.id, j.joke.text, j.joke.createdDate, j.joke.updatedDate, COUNT(j)) " +
            "FROM JokeCalls j " +
            "GROUP BY j.joke.id, j.joke.text, j.joke.createdDate, j.joke.updatedDate " +
            "ORDER BY COUNT(j) DESC " +
            "LIMIT 5")
    List<JokeWithCount> findTop5JokesByCalls();
}
