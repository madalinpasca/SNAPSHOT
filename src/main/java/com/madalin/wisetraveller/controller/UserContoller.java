package com.madalin.wisetraveller.controller;

import com.madalin.wisetraveller.dto.*;
import com.madalin.wisetraveller.dto.mapper.UserMapper;
import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.service.FileService;
import com.madalin.wisetraveller.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserContoller {
    private UserService userService;
    private UserMapper userMapper;
    private FileService fileService;

    @GetMapping("/authenticated/getAll")
    public ResponseEntity<?> buuuun (){
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/authorize/google")
    public ResponseEntity<AuthorizationCodeDto> authorizationCodeGoogle(@RequestBody GoogleIdTokenDto tokenDto) {
        return new ResponseEntity<>(new AuthorizationCodeDto(
                userService.getAuthorizationCodeGoogle(tokenDto.getValue(), tokenDto.getPhoneNumber())),
                HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/authorize/facebook")
    public ResponseEntity<AuthorizationCodeDto> authorizationCodeFacebook(@RequestBody FacebookAccessTokenDto tokenDto) {
        return new ResponseEntity<>(new AuthorizationCodeDto(
                userService.getAuthorizationCodeFacebook(tokenDto.getValue(), tokenDto.getPhoneNumber())),
                HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/addUser")
    public ResponseEntity<IdWrapper> addUser(@RequestBody RegisterDto registerDto) {
        return new ResponseEntity<>(new IdWrapper(userService.register(userMapper.mapRegisterDtoUser(registerDto))),
                HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/upload-avatar/{id}")
    public void upload(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        User user = userService.get(id);
        fileService.saveAsImage(file, userService.getAvatarPath(user));
        userService.changeAvatarPath(user, fileService.getImageExtension(file));
    }

    @GetMapping("/unauthenticated/activate")
    public ResponseEntity<?> activateUser(@RequestParam String uuid, @RequestParam Long id) {
        userService.activate(uuid, id);
        return new ResponseEntity<>("<script type=\"text/javascript\">window.close();</script>", HttpStatus.OK);
    }

    @PostMapping("/unauthenticated/resetPassword/")
    public void resetPassword(@RequestBody ResetPasswordDto dto) {
        userService.resetPassword(dto.getEmail(), dto.getPassword());
    }

    @GetMapping("/unauthenticated/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String uuid, @RequestParam Long id) {
        userService.resetPassword(uuid, id);
        return new ResponseEntity<>("<script type=\"text/javascript\">window.close();</script>", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/unauthenticated/avatar/{userId}.png", produces = "image/png")
    public ResponseEntity<?> pngAvatar(@PathVariable Long userId) {
        return getAvatar(userId, "png");
    }

    @ResponseBody
    @GetMapping(value = "/unauthenticated/avatar/{userId}.jpeg", produces = "image/jpeg")
    public ResponseEntity<?> jpgAvatar(@PathVariable Long userId) {
        return getAvatar(userId, "jpeg");
    }

    @ResponseBody
    @GetMapping(value = "/unauthenticated/avatar/{userId}.bmp", produces = "image/bmp")
    public ResponseEntity<?> bmpAvatar(@PathVariable Long userId) {
        return getAvatar(userId, "bmp");
    }

    private ResponseEntity<?> getAvatar(Long userId, String extension) {
        Resource resource = fileService.loadAsResource(userService.getAvatarPath(userId, extension));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PostMapping("/authenticated/me")
    public ResponseEntity<UserDto> getDetails() {
        return new ResponseEntity<>(userMapper.mapUser(userService.getCurrent()), HttpStatus.OK);
    }
}
