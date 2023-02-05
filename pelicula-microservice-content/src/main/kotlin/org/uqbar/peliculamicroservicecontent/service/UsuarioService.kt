package org.uqbar.peliculamicroservicecontent.service

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

    fun authorize(_token: String): Boolean {
        token = _token
        val authRequest = RequestEntity.get("${authBaseUrl}/auth/validate")
            .headers(HttpHeaders().apply {
                setBearerAuth(token)
            })
            .build()
        val authResponse = RestTemplate().exchange(authRequest, String::class.java)
        return authResponse.body == "ok"
    }
}