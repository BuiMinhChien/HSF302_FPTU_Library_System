package com.mss301.fe.edu.vn.hsf302_fptu_library_system.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // disable csrf nếu test Postman
                // nếu chỉ dùng thymeleaf thì nên enable
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // public pages
                        .requestMatchers(
                                "/login",
                                "/forgot-password",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        // LIBRARIAN
                        .requestMatchers("/librarian/**")
                        .hasRole("LIBRARIAN")
                        // READER
                        .requestMatchers("/reader/**")
                        .hasRole("READER")
                        // PayOS callback
                        .requestMatchers("/payment/payos/**")
                        .permitAll()
                        // USER
                        .requestMatchers("/user/**")
                        .hasRole("USER")
                        // tất cả request khác cần login
                        .anyRequest()
                        .authenticated()
                )
                // login bằng form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/books", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // access denied
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}