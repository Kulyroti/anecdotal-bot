package ru.java_bot.anecdotal_bot.model;

import java.time.LocalDateTime;

public class JokeWithCount extends JokeModel{
    public JokeWithCount(Long id, String text, LocalDateTime createdDate, LocalDateTime updatedDate, Long count) {
        super(id, text, createdDate, updatedDate);
        this.count = count;
    }

    public long count;
}
