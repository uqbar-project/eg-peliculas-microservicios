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
import org.uqbar.peliculasmicroserviceauth.dto.CredencialesDTO
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.repository.UsuarioRepository

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

   @BeforeEach
   fun crearUsuarios() {
      usuarioRepository.deleteAll()
      crearUsuario("user1", "password1")
      crearUsuario("admin", "123456")
   }

   // region: login
   @Test
   fun `usuario inexistente no pasa el login`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioInexistente())
      ).andReturn().response
      assertEquals(401, responseEntity.status)
   }

   @Test
   fun `usuario con password incorrecta no pasa el login`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioPasswordIncorrecta())
      ).andReturn().response
      assertEquals(401, responseEntity.status)
   }

   @Test
   fun `usuario existente pasa el login y retorna JWT`() {
      val responseEntity = mockMvc.perform(
         post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyUsuarioExistente())
      ).andReturn().response
      assertEquals(200, responseEntity.status)
   }
   // endregion

   private fun bodyUsuarioExistente() = mapper.writeValueAsString(CredencialesDTO("user1", "password1"))

   private fun bodyUsuarioPasswordIncorrecta() = mapper.writeValueAsString(CredencialesDTO("user1", "password2"))

   private fun bodyUsuarioInexistente() =
      mapper.writeValueAsString(CredencialesDTO("usuarioInvalido", "cualquierPassword"))

   private fun crearUsuario(_nombre: String, _password: String) {
      usuarioRepository.save(Usuario().apply {
         nombre = _nombre
         crearPassword(_password)
      })
   }

}
