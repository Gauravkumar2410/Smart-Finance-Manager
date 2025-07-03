package com.expensetracker.expensetracker.config;

import com.expensetracker.expensetracker.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint; // Import for LoginUrlAuthenticationEntryPoint


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to static resources (CSS, JS, Images)
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        // Allow public access to registration pages
                        .requestMatchers("/register", "/process_register").permitAll()
                        // Allow public access to the login page itself
                        .requestMatchers("/login").permitAll() // Explicitly permit login page
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                                .loginPage("/login") // Specify the custom login page
                                .usernameParameter("email") // Use 'email' as the username parameter
                                .defaultSuccessUrl("/", true) // Redirect to the root URL (dashboard) after successful login, always
                        // Removed redundant .permitAll() here as /login is already permitted by requestMatchers
                )
                .logout(logout -> logout
                                .logoutSuccessUrl("/login?logout") // Redirect to login page with logout message after logout
                        // Removed redundant .permitAll() here as logout is implicitly handled or covered
                )
                // Explicitly define the authentication entry point for unauthenticated requests
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                );
        // Updated way to disable CSRF, replacing the deprecated .csrf().disable()
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
