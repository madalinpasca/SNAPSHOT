package com.madalin.wisetraveller.dto;

import com.madalin.wisetraveller.model.enums.Role;
import com.madalin.wisetraveller.model.enums.TipUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String email;
    private String urlProfile;
    private TipUser userType;
    private Set<Role> roles;
}
