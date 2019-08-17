package com.madalin.wisetraveller.dto.mapper;

import com.madalin.wisetraveller.config.PasswordEncoders;
import com.madalin.wisetraveller.dto.RegisterDto;
import com.madalin.wisetraveller.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserMapper {
    PasswordEncoder userPasswordEncoder;

    public User mapRegisterDtoUser(RegisterDto registerDto) {
        User user = new User();
        user.setNume(registerDto.getFirstName());
        user.setPrenume(registerDto.getLastName());
        user.setTelefon(registerDto.getPhoneNumber());
        user.setEmail(registerDto.getEmail());
        user.setParola(userPasswordEncoder.encode(registerDto.getPassword()));
        return user;
    }
}
