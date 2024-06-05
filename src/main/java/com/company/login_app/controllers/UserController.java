package com.company.login_app.controllers;

import com.company.login_app.dto.UserDTO;
import com.company.login_app.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final RegistrationService service;

    @PostMapping("/register")
    public String saveUser(@RequestBody UserDTO userDTO){
        return service.register(userDTO);
    }


    @GetMapping("/register/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token){
        return ResponseEntity.ok(service.confirm(token));
    }



}
