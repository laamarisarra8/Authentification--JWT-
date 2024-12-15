package com.sarra.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity // sinse we are not using reactive(what does reactive means ?) we need to add this not the others
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // for the role based authentification
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtAuthFilter;
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)throws Exception{
       http
               .cors(withDefaults()) // spring will seach for any definition of CORS filter and it will the config we provided "it's in BeansConfig class"
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests(req ->
                       req.requestMatchers(
                                       "/auth/**",
                                       "/v2/api-docs",
                                       "/v3/api-docs",
                                       "/v3/api-docs/**",
                                       "/swagger-resources",
                                       "/swagger-resources/**",
                                       "/configuration/ui",
                                       "/configuration/security",
                                       "/swagger-ui/**",
                                       "/webjars/**",
                                       "/swagger-ui.html"
                       ).permitAll() //allow these requests
                               .anyRequest() // but make these authentificated
                               .authenticated()

                   )
               .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
               .authenticationProvider(authenticationProvider)
               .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // here we are adding Filters to the Filter Chain #addFilterBefore(Filter, Class<?>)
       return http.build();                                                                 // wich means that the jwtAuthFilter IS invoked Before the UsernamePasswordAuthenticationFilter

    }
}
