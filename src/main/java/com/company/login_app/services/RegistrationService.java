package com.company.login_app.services;

import com.company.login_app.dto.UserDTO;
import com.company.login_app.models.AppUser;
import com.company.login_app.models.Token;
import com.company.login_app.models.UserRole;
import com.company.login_app.repositories.TokenRepository;
import com.company.login_app.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    private final static  String CONFIRMATION_URL="http://localhost:8080/register/confirm?token=%s";
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
        AppUser user = repository.save(appUser);

        //Token creation
        String generatedToken= UUID.randomUUID().toString();
        Token token= Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);

        try {
            emailService.send(userDTO.getEmail(), user.getFirstname(), null, String.format(CONFIRMATION_URL,generatedToken));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return generatedToken;
    }

    public String confirm(String token){
        Token savedToken=tokenRepository.findByToken(token).
                orElseThrow(()-> new IllegalStateException("Token not found"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiredAt())){
            String newGeneratedToken= UUID.randomUUID().toString();
            Token newToken= Token.builder()
                    .token(newGeneratedToken)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                    .user(savedToken.getUser())
                    .build();
            tokenRepository.save(newToken);

            try {
                emailService.send(savedToken.getUser().getEmail(), savedToken.getUser().getFirstname(), null, String.format(CONFIRMATION_URL,newGeneratedToken));
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return "Token expired, New token has been sent your email";
        }
        AppUser user= repository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("user not found"));

        user.setEnabled(true);
        repository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        return "<h1>Your account has been successfully activated</h1>";
    }
}

