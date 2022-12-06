package org.uqbar.peliculasmicroserviceauth.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
   lateinit var authConfiguration: AuthenticationConfiguration

   @Autowired
   lateinit var jwtAuthorizationFilter: JWTAuthorizationFilter

   @Bean
   @Throws(Exception::class)
   fun authenticationManager(): AuthenticationManager? {
      return authConfiguration.getAuthenticationManager()
   }

   @Bean
   fun filterChain(httpSecurity: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
      return httpSecurity
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
          // agregado para JWT, si comentás estas dos líneas tendrías Basic Auth
         .and()
         .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
          // fin agregado
         .build()
   }
}