package com.sarra.book.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message="Firstname is mandatory")
    @NotBlank(message="Firstname is mandatory")
    private String firstname;

    @NotEmpty(message="lastname is mandatory")
    @NotBlank(message="lastname is mandatory")
    private  String lastname;

    @Email(message = "Email is not formated")
    @NotEmpty(message="Email is mandatory")
    @NotBlank(message="Email is mandatory")
    private String email;

    @Size(min= 8, message = "password should be 8 caracter minimum")
    @NotEmpty(message="Password is mandatory")
    @NotBlank(message="Password is mandatory")
    private String password;
}
