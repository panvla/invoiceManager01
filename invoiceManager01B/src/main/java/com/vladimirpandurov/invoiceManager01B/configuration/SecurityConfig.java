package com.vladimirpandurov.invoiceManager01B.configuration;

import com.vladimirpandurov.invoiceManager01B.handler.CustomAccessDeniedHandler;
import com.vladimirpandurov.invoiceManager01B.handler.CustomAutehnticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAutehnticationEntryPoint customAutehnticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final BCryptPasswordEncoder encoder;
    private final UserDetailsService userDetailsService;

    private static final String[] PUBLIC_URLS = {"/user/login/**", "/user/verify/code/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf().disable().cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().requestMatchers(PUBLIC_URLS).permitAll();
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("DELETE_uSER");
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/customer/delete/**").hasAnyAuthority("DELETE:CUSTOMER");
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAutehnticationEntryPoint);
        http.authorizeHttpRequests().anyRequest().authenticated();
        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

}
