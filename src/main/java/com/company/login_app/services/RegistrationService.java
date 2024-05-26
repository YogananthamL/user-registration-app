package com.company.login_app.services;

import com.company.login_app.dto.UserDTO;
import com.company.login_app.models.AppUser;
import com.company.login_app.models.UserRole;
import com.company.login_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public String register(UserDTO userDTO){
        //check if the user already exist
        boolean userExists=repository.findByEmail(userDTO.getEmail()).isPresent();
        if(userExists){
            throw new IllegalStateException("User with same email already exist");
        }

        //Encoding password using BCRYPT
        String encodedPassword=encoder.encode(userDTO.getPassword());
        //Data Transfer Object to User
        AppUser appUser = AppUser.builder()
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .email(userDTO.getEmail())
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        //Save user
        repository.save(appUser);
        return "User has been registered successfully";
    }
}
