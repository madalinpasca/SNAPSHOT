package com.madalin.wisetraveller.model;

import com.madalin.wisetraveller.model.enums.TipUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="\"User\"")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String nume;

    @Column
    private String prenume;

    @Column
    private String telefon;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String parola;

    @Column (nullable = true)
    private String userProvidedId;

    @Column(nullable = true)
    private TipUser tipUser;

}
