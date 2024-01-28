package com.neov.epassi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  
  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
    return http
             .authorizeExchange((authorize) -> authorize
                                                 .pathMatchers(HttpMethod.GET, "/**").permitAll()
                                                 .pathMatchers(HttpMethod.POST, "/api/freqency")
                                                 .hasRole("USER")
             )
             .httpBasic(Customizer.withDefaults())
             .csrf(ServerHttpSecurity.CsrfSpec::disable)
             .build();
  }
  
  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User
                           .withUsername("user")
                           .password("{noop}password")
                           .roles("USER")
                           .build();
    return new MapReactiveUserDetailsService(user);
  }
  
}
