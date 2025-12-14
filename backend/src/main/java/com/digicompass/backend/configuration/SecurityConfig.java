package com.digicompass.backend.configuration;

import com.digicompass.backend.application.security.JWTAuthFilter;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JWTAuthFilter jwtAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        //PUBLIC ENDPOINTS
                        .requestMatchers("/auth/**", "/users/**","/password/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rating/route/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/route/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/route/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/route/calculate-distance").permitAll()

                        //PROTECTED ENDPOINTS
                        .requestMatchers(HttpMethod.DELETE, "/route/delete/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/route/create")
                        .hasAnyRole("USER", "ADMIN")

                        //EVERYTHING ELSE
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}


