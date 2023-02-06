package org.uqbar.peliculasmicroserviceranking

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.uqbar.peliculasmicroserviceranking.domain.Genero
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.repository.GeneroRepository
import org.uqbar.peliculasmicroserviceranking.repository.PeliculaRepository
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PeliculaControllerTests {

	private val mapper = jacksonObjectMapper().apply {
		registerModule(JavaTimeModule())
	}

	@Autowired
	lateinit var mockMvc: MockMvc

	companion object {
		@Autowired
		lateinit var peliculaRepository: PeliculaRepository

		@Autowired
		lateinit var generoRepository: GeneroRepository

		@BeforeAll
		fun setup() {
			val comedia = Genero().apply {
				descripcion = "Comedia"
				idOriginal = 1
			}
			generoRepository.save(comedia)

			peliculaRepository.save(Pelicula().apply {
				idioma = "ES"
				sinopsis = "La confusión invade a una familia luego de que Mamá Cora, una anciana de 80 años, desaparece. Aunque algunos creen que ha muerto, nada es tan sencillo como parece."
				fechaSalida = LocalDate.of(1985, 4, 1)
				calificacionPromedio = 7.5
				generos = mutableListOf(comedia)
			})
		}

		@AfterAll
		fun destroy() {
			peliculaRepository.deleteAll()
			generoRepository.deleteAll()
		}
	}

	@Test
	fun `un usuario inexistente no puede consultar películas`() {
		mockMvc.perform(
			MockMvcRequestBuilders.post("/graphql")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bodyUsuarioInexistente())
		).andExpect(MockMvcResultMatchers.status().isUnauthorized)
	}

//	TODO: hay que buscar un usuario existente y mockear que auth devuelva ok
//	@Test
//	fun `un usuario existente puede consultar una película`() {
//		mockMvc.perform(
//			MockMvcRequestBuilders.post("/graphql")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(bodyUsuarioExistente())
//		).andExpect(MockMvcResultMatchers.status().isUnauthorized)
//	}

	private fun bodyUsuarioExistente() = mapper.writeValueAsString(CredencialesDTO("user1", "password1"))

	private fun bodyUsuarioInexistente() =
		mapper.writeValueAsString(CredencialesDTO("usuarioInvalido", "cualquierPassword"))

}

data class CredencialesDTO(val usuario: String, val password: String)
