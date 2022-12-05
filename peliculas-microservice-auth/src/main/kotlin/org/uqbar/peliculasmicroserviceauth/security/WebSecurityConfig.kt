package org.uqbar.peliculasmicroserviceauth.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
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
//         .requestMatchers(HttpMethod.POST,"**").permitAll()
//         .anyRequest().authenticated()
         .anyRequest().permitAll()
         .and()
         .httpBasic()
         .and()
         .sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         .and()
         .build()

//   @Bean
//   fun passwordEncoder() = BCryptPasswordEncoder()

//   @Bean
//   fun authenticationManager(httpSecurity: HttpSecurity) =
//      httpSecurity
//         .getSharedObject(AuthenticationManager::class.java)
//         .userDetailsService(userDetailsService())
//         .passwordEncoder(passwordEncoder())
//         .and()
//         .build()
}