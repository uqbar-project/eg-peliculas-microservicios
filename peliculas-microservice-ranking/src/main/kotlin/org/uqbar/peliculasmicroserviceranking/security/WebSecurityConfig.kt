package org.uqbar.peliculasmicroserviceranking.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

   @Autowired
   lateinit var jwtAuthorizationFilter: JWTAuthorizationFilter

//   Ojo con definir authenticationManager como Bean, ver...
//   https://stackoverflow.com/questions/73883122/i-get-stackoverflowerror-in-spring-security-test
//   https://github.com/spring-projects/spring-framework/issues/29215

   @Bean
   fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
      return httpSecurity
         .cors().disable()
         .csrf().disable()
         .authorizeHttpRequests()
         .requestMatchers("/error").permitAll()
         .requestMatchers("/graphql").permitAll()
         .anyRequest().authenticated()
         .and()
         .httpBasic()
         .and()
         .sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          // agregado para JWT, si comentás estas dos líneas tendrías Basic Auth
         .and()
         .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
          // fin agregado
         .exceptionHandling()
         .and()
         .build()
   }
}