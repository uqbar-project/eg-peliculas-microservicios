package org.uqbar.peliculasmicroserviceauth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenUtils {
   @Value("\${security.secret-key}")
   lateinit var secretKey: String

   @Value("\${security.access-token-minutes}")
   var accessTokenMinutes: Int = 60

   fun createToken(nombre: String, password: String): String? {
      val longExpirationTime = accessTokenMinutes * 60 * 60 * 1000
      val expirationDate = Date(System.currentTimeMillis() + longExpirationTime)

      val extra = mutableMapOf<String, Any>().apply {
         put("nombre", nombre)
      }

      return Jwts.builder()
         .setSubject(password)
         .setExpiration(expirationDate)
         .setClaims(extra)
         .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
         .compact()
   }

   fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
      val claims = Jwts.parserBuilder()
         .setSigningKey(secretKey.toByteArray())
         .build()
         .parseClaimsJws(token)
         .body

      return UsernamePasswordAuthenticationToken(claims.subject, null, listOf())
   }
}