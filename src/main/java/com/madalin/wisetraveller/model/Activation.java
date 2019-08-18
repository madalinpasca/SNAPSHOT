package com.madalin.wisetraveller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "activation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String uuid;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    private boolean activated;
}
