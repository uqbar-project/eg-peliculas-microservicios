package org.uqbar.peliculasmicroserviceauth.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

   @Bean
   fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
      httpSecurity
         .cors().disable()
         .csrf().disable()
         .authorizeHttpRequests()
         .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
         .anyRequest().authenticated()
         .and()
         .httpBasic()
         .and()
         .sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         .and()
         .build()

}