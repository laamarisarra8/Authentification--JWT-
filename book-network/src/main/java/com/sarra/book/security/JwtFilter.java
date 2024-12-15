package com.sarra.book.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//@AllArgsConstructor
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { //JWT filter (JwtFilter) that will intercept and process every incoming HTTP request. The purpose of this filter is to handle JWT-based authentication by examining the request for valid tokens before passing it to the rest of the application.

    private final jwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(  //this methode will be executed every time we have an HTTP request
                                  @NonNull HttpServletRequest request, // Represents the incoming HTTP request.
                                  @NonNull HttpServletResponse response, // the response that will be sent back to the client.
                                  @NonNull FilterChain filterChain // to pass the request and response to the next filter in the chain, or to the endpoint if no other filters are left.
    ) throws ServletException, IOException {
        if(request.getServletPath().contains("/api/v1/auth")){
            filterChain.doFilter(request, response); //This method passes the current HTTP request and response objects to the next filter in the filter chain.
            return; // It ensures clean, predictable behavior by immediately stopping further execution for skipped requests.
        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // the token is sent in the header and the header is called AUTHORIZATION
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // this means tha the user is not authenticated
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}