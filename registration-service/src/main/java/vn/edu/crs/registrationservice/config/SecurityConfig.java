package vn.edu.crs.registrationservice.config;

import vn.edu.crs.registrationservice.security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // REST API sử dụng JWT nên tắt CSRF
                .csrf(csrf ->
                        csrf.disable()
                )

                // Không sử dụng HTTP Session
                // Mỗi request phải mang JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Tất cả API registrations
                        // phải có JWT hợp lệ
                        .requestMatchers(
                                "/registrations/**"
                        )
                        .authenticated()

                        // Endpoint khác cho phép
                        .anyRequest()
                        .permitAll()
                )

                // Chạy JwtAuthFilter trước filter mặc định
                // của Spring Security
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}