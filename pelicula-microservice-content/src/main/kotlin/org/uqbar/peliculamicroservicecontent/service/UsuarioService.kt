package org.uqbar.peliculamicroservicecontent.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UsuarioService {

    @Value("\${auth.base-url}")
    lateinit var authBaseUrl: String

    lateinit var token: String

    val logger: Logger = LoggerFactory.getLogger(UsuarioService::class.java)

    fun authorize(_token: String): Boolean {
        token = _token
        val authRequest = RequestEntity.get("${authBaseUrl}/auth/validate")
            .headers(HttpHeaders().apply {
                setBearerAuth(token)
            })
            .build()
        return try {
            val authResponse = RestTemplate().exchange(authRequest, String::class.java)
            logger.info("authorization passed: ${authResponse.body}")
            authResponse.body == "ok"
        } catch (e: Exception) {
            logger.info("authorization failed: ${e.message}")
            false
        }
    }
}