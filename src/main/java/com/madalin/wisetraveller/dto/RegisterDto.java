package com.madalin.wisetraveller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RegisterDto {
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String email;
    private String password;
}
