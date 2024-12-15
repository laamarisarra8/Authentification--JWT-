package com.sarra.book.auth;

import com.sarra.book.email.EmailService;
import com.sarra.book.email.EmailTemplateName;
import com.sarra.book.role.RoleRepository;
import com.sarra.book.security.jwtService;
import com.sarra.book.user.Token;
import com.sarra.book.user.TokenRepository;
import com.sarra.book.user.User;
import com.sarra.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom; //class in Java that provides cryptographically strong random numbers.
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final jwtService jwtService;


    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;



    public void register(RegistrationRequest request) throws MessagingException {
        //we need the fetch the role and assign it to the user
        //we need to crete a user object and save it
        //we need to send a validation email

        var userRole = roleRepository.findByName("USER")
                // to do better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        var user = User.builder() // here we create a user Object
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // the password will be encoded when persisted to the data base
                .accountLocked(false) // by default the account is locked
                .enabled(false) //flag indicates that the user must validate using the activation code
                .roles(List.of(userRole))
                .build();
        userRepository.save(user); // we persiste user to the database
        sendValidationEmail(user); // before sending we need to generate and ave the activation token


    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        //send email

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"


        );

    }

    private String generateAndSaveActivationToken(User user) {
        //Generat the token
        String generateToken = generateActivationCode(6); // the code is generated here
        var token = Token.builder()
                .token(generateToken)
                .createdAt(LocalDateTime.now()) // when it's created
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // when the token expires
                .user(user) // the owner of the token
                .build();
        tokenRepository.save(token); //here we are saving the token to the database
        return generateToken; // we return the generated token

    }

    private String generateActivationCode(int lenght) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder(); // une classe utilisée pour créer une succession de caractères mutable, c'est-à-dire modifiable
        SecureRandom secureRandom = new SecureRandom(); // we need to make it Secure Random so that it will make Cryptographycaly secure
        for (int i = 0; i < lenght; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); //0...9

            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) { // this methode will take care of the authentification process f everything is correct it will return the authentification otherwise it wil throw an exception
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user= ((User)auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }


    //@Transactional
    public void activationAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("invalis Token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){ //if the token is expired
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has Expired.A new token has been sent to the same email addres");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
