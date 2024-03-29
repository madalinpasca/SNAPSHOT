package com.madalin.wisetraveller.dto.mapper;

import com.madalin.wisetraveller.dto.RegisterDto;
import com.madalin.wisetraveller.dto.UserDto;
import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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

    public UserDto mapUser(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getPrenume());
        dto.setLastName(user.getNume());
        dto.setPhoneNumber(user.getTelefon());
        dto.setRoles(user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()));
        dto.setUserType(user.getTipUser());
        dto.setUrlProfile(user.getUrlProfil());
        return dto;
    }
}
