package ru.java_bot.anecdotal_bot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "jokes")
@Table(name = "jokes")
public class JokeModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "текст шутки")
    private String text;

    @Column(name = "дата создания")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "дата обновления")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
