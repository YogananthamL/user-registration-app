package com.company.login_app.controllers;

import com.company.login_app.dto.UserDTO;
import com.company.login_app.models.AppUser;
import com.company.login_app.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final RegistrationService service;

    @PostMapping("/register")
    public String saveUser(@RequestBody UserDTO userDTO){
        return service.register(userDTO);
    }
}
