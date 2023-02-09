package org.uqbar.peliculasmicroserviceranking

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.http.JvmProxyConfigurer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.uqbar.peliculasmicroserviceranking.domain.Genero
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.repository.GeneroRepository
import org.uqbar.peliculasmicroserviceranking.repository.PeliculaRepository
import org.uqbar.peliculasmicroserviceranking.service.TMDBService
import reactor.core.publisher.Mono
import java.time.LocalDate


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureGraphQlTester
class PeliculaControllerTests {

    private val mapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    @Autowired
    lateinit var peliculaRepository: PeliculaRepository

    @Autowired
    lateinit var generoRepository: GeneroRepository

    lateinit var wireMockServer: WireMockServer

    @MockBean
    lateinit var tmdbService: TMDBService

    @BeforeEach
    fun setup() {
        val comedia = Genero().apply {
            descripcion = "Comedia"
            idOriginal = 1
        }
        generoRepository.save(comedia)

        val pelicula = Mono.defer {
            peliculaRepository.save(Pelicula().apply {
                idioma = "ES"
                titulo = "Esperando la carroza"
                sinopsis =
                    "La confusión invade a una familia luego de que Mamá Cora, una anciana de 80 años, desaparece. Aunque algunos creen que ha muerto, nada es tan sencillo como parece."
                fechaSalida = LocalDate.of(1985, 4, 1)
                calificacionPromedio = 7.5
                generos = mutableListOf(comedia)
            })
        }.block()

        wireMockServer = WireMockServer(
            options()
                .enableBrowserProxying(true)
        )
        wireMockServer.start()

        JvmProxyConfigurer.configureFor(wireMockServer)

        wireMockServer.stubFor(
            get("/auth/validate")
                .willReturn(ok("ok"))
                .withHost(
                    equalTo("localhost")
                )
        )

        Mockito.`when`(tmdbService.buscarPeliculaPorId(anyInt())).thenReturn(pelicula)
    }

    @AfterEach
    fun destroy() {
        peliculaRepository.deleteAll()
        generoRepository.deleteAll()

        wireMockServer.stop()
    }

    @Test
    fun `un usuario inexistente no puede consultar una película`() {
        wireMockServer.stubFor(
            get("/auth/validate")
                .willReturn(jsonResponse("""
                    {
                    	"timestamp": "2023-02-09T02:51:00.571+00:00",
                    	"status": 500,
                    	"error": "Internal Server Error",
                    	"message": "JWT expired at 2022-12-22T02:08:49Z. Current time: 2023-02-09T02:51:00Z, a difference of 4236131569 milliseconds.  Allowed clock skew: 0 milliseconds.",
                    	"path": "/auth/validate"
                    }
                """.trimIndent(), HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .withHost(
                    equalTo("localhost")
                )
        )

        graphQlTester.document(
            """
			query {
			  peliculaPorIdTMDB(idTMDB: 1) {
				titulo
			  }
			}
		""".trimIndent()
        )
            .execute()
            .path("")
            .matchesJson("{ }")
        // TODO: ver por qué no está tirando
        // Está pasando por JWT?
        /**
         * {
         * 	"timestamp": "2023-02-09T02:59:13.726+00:00",
         * 	"status": 500,
         * 	"error": "Internal Server Error",
         * 	"message": "500 : \"{\"timestamp\":\"2023-02-09T02:59:13.709+00:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"JWT expired at 2022-12-22T02:08:49Z. Current time: 2023-02-09T02:59:13Z, a difference of 4236624708 milliseconds.  Allowed clock skew: 0 milliseconds.\",\"path\":\"/auth/validate\"}\"",
         * 	"path": "/graphql"
         * }
         */
    }

    @Test
    fun `un usuario existente puede consultar una pelicula`() {
        graphQlTester.document(
            """
			query {
			  peliculaPorIdTMDB(idTMDB: 1) {
				titulo
			  }
			}
		""".trimIndent()
        )
            .execute()
            .path("peliculaPorIdTMDB")
            .hasValue()
            .entity(Pelicula::class.java)
            .matches { pelicula -> pelicula.titulo.equals("Esperando la carroza")  }
    }

}

data class CredencialesDTO(val usuario: String, val password: String)
