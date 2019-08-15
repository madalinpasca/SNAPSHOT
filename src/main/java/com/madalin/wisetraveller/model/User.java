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

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private String prenume;

    @Column
    private String telefon;

    @Column(nullable = false)
    private String email;

    @Column
    private String parola;

    @Column
    private String userProvidedId;

    @Column
    private TipUser tipUser;

    @Column
    private String urlProfil;
}
