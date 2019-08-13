package com.madalin.wisetraveller.controller;

import com.madalin.wisetraveller.dto.AuthorizationCodeDto;
import com.madalin.wisetraveller.dto.FacebookAccessTokenDto;
import com.madalin.wisetraveller.dto.GoogleIdTokenDto;
import com.madalin.wisetraveller.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserContoller {
    private UserService userService;

    @GetMapping("/authenticated/getAll")
    public ResponseEntity<?> buuuun (){
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/authorize/google")
    public ResponseEntity<AuthorizationCodeDto> authorizationCodeGoogle(@RequestBody GoogleIdTokenDto tokenDto) {
        return new ResponseEntity<>(new AuthorizationCodeDto(
                userService.getAuthorizationCodeGoogle(tokenDto.getValue())), HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/authorize/facebook")
    public ResponseEntity<AuthorizationCodeDto> authorizationCodeFacebook(@RequestBody FacebookAccessTokenDto tokenDto) {
        return new ResponseEntity<>(new AuthorizationCodeDto(
                userService.getAuthorizationCodeFacebook(tokenDto.getValue())), HttpStatus.OK);
    }
}
