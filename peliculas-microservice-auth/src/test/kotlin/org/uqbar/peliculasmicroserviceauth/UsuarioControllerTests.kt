package org.uqbar.peliculasmicroserviceauth

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.uqbar.peliculasmicroserviceauth.dto.CredencialesDTO
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.repository.UsuarioRepository
import org.uqbar.peliculasmicroserviceauth.security.TokenUtils

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests de Usuario")
class UsuarioControllerTests {

   private val mapper = jacksonObjectMapper().apply {
      registerModule(JavaTimeModule())
   }

   @Autowired
   lateinit var mockMvc: MockMvc

   @Autowired
   lateinit var usuarioRepository: UsuarioRepository

   @Autowired
   lateinit var tokenUtils: TokenUtils

   lateinit var tokenUsuarioOk: String

   lateinit var tokenAdminOk: String

   @BeforeEach
   fun crearUsuarios() {
      usuarioRepository.deleteAll()
      tokenUsuarioOk = crearUsuario("user1", "password1")
      tokenAdminOk = crearUsuario("admin", "123456")
   }

   // region: login
   @Test
   fun `usuario inexistente no pasa el login`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioInexistente())
      ).andExpect(status().isUnauthorized)
   }

   @Test
   fun `usuario con password incorrecta no pasa el login`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioPasswordIncorrecta())
      ).andExpect(status().isUnauthorized)
   }

   @Test
   fun `usuario existente pasa el login y retorna JWT`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioExistente())
      )
         .andExpect(status().isOk)
   }
   // endregion

   // region /users
   @Test
   fun `no se pueden conocer los usuarios si no pasamos un token correcto`() {
      val responseEntity = mockMvc.perform(
         get("/auth/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", tokenUsuarioInvalido())
      ).andExpect(status().isUnauthorized)
   }

   @Test
   fun `se pueden ver los usuarios con un token correcto`() {
      val responseEntity = mockMvc.perform(
         get("/auth/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", tokenUsuarioOk)
      )
         .andExpect(status().isOk)
         .andExpect(jsonPath("$[0].nombre").value("user1"))
         .andExpect(jsonPath("$[1].nombre").value("admin"))
   }
   // end region

   private fun bodyUsuarioExistente() = mapper.writeValueAsString(CredencialesDTO("user1", "password1"))

   private fun bodyUsuarioPasswordIncorrecta() = mapper.writeValueAsString(CredencialesDTO("user1", "password2"))

   private fun bodyUsuarioInexistente() =
      mapper.writeValueAsString(CredencialesDTO("usuarioInvalido", "cualquierPassword"))

   private fun tokenUsuarioInvalido() =
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsaXR1cmJlIiwiaWF0IjoxNjcwNTk0OTI5LCJleHAiOjE2NzE2NzQ5MjksInJvbGVzIjoiUk9MRV9VU0VSIn0.hSrd0sTw1OH57YlmV19xNCtide76AZa476XjPwE1uiW0wgbo7w5CarrJWCLjy0e62EZIbVjEGmIdHZ5tMHGkyg"

   private fun crearUsuario(_nombre: String, _password: String): String {
      val usuario = Usuario().apply {
         nombre = _nombre
         crearPassword(_password)
      }
      usuarioRepository.save(usuario)
      return "Bearer " + tokenUtils.createToken(usuario.nombre, usuario.password)!!
   }

}
