package io.mountblue.BlogApplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager theUserDetailManager =  new JdbcUserDetailsManager(dataSource);

        theUserDetailManager.setUsersByUsernameQuery("select username,password,true from user where username = ?");

        theUserDetailManager.setAuthoritiesByUsernameQuery("select username, role from user where username = ?");


        return theUserDetailManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/", "/features", "/post{postId}").permitAll()
                                .requestMatchers("/newpost", "/drafts", "/update**", "/delete**").authenticated()
                                .requestMatchers(HttpMethod.POST,"/api/createPost").authenticated()
                                .requestMatchers("/api/user").authenticated()
                                .requestMatchers(HttpMethod.DELETE,"/api/deletePost/{postId}", "/{postId}/deleteComment/{commentId}").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/updatePost/{postId}", "/api/{postId}/updateComment/{commentId}").authenticated()
                                .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
