package ru.java_bot.anecdotal_bot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "jokes")
public class JokeModel {

    @Id
    @GeneratedValue(generator = "model_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "model_id_seq", sequenceName = "model_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "joke_text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "creation_date")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Column(name = "updation_date")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedDate;

}
