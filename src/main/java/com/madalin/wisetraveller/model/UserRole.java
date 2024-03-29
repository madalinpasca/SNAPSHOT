package com.madalin.wisetraveller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.madalin.wisetraveller.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Role role;

    @ManyToOne(optional = false)
    @JsonIgnore
    private User user;
}
