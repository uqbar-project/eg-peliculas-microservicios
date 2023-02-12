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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.uqbar.peliculasmicroserviceranking.domain.Genero
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.repository.GeneroRepository
import org.uqbar.peliculasmicroserviceranking.repository.PeliculaRepository
import org.uqbar.peliculasmicroserviceranking.service.UsuarioService
import java.time.LocalDate


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
class PeliculaControllerTests {

    private val mapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    @Autowired
    lateinit var peliculaRepository: PeliculaRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var generoRepository: GeneroRepository

    @Autowired
    lateinit var usuarioService: UsuarioService

    lateinit var wireMockServer: WireMockServer

    val tokenUsuarioInexistente = "tokenInexistente"

    @BeforeEach
    fun setup() {
        val comedia = crearGenero("Comedia")
        val suspenso = crearGenero("Suspenso")

        peliculaRepository.save(Pelicula().apply {
            idioma = "ES"
            titulo = "Esperando la carroza"
            vistas = 110
            idTMDB = 1200
            sinopsis =
                "La confusión invade a una familia luego de que Mamá Cora, una anciana de 80 años, desaparece. Aunque algunos creen que ha muerto, nada es tan sencillo como parece."
            fechaSalida = LocalDate.of(1985, 4, 1)
            calificacionPromedio = 7.5
            generos = mutableListOf(comedia)
        })

        peliculaRepository.save(Pelicula().apply {
            idioma = "ES"
            idTMDB = 144
            titulo = "Nueve reinas"
            vistas = 201
            sinopsis =
                "Dos estafadores se hacen socios en busca de su sueño, vender una falsificación de una serie de estampillas que los hará dar el gran golpe."
            fechaSalida = LocalDate.of(1999, 10, 2)
            calificacionPromedio = 8.2
            generos = mutableListOf(suspenso)
        })

        wireMockServer = WireMockServer(
            options()
                .enableBrowserProxying(true)
                .port(9080)
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

        wireMockServer.stubFor(
            get("/auth/users/user1")
                .willReturn(jsonResponse("""
                    {
                        "id": 5,
                        "nombre": "Usuario Calificador"
                    }
                """.trimIndent(), HttpStatus.OK.value()))
        )

        wireMockServer.stubFor(
            get("/movie/1?api_key=11111111111111&language=en-US")
                .willReturn(
                    ok(
                        """
                                {
                                	"budget": 200000000,
                                	"genres": [
                                		{
                                			"id": 14,
                                			"name": "Fantasy"
                                		},
                                		{
                                			"id": 28,
                                			"name": "Action"
                                		},
                                		{
                                			"id": 878,
                                			"name": "Science Fiction"
                                		}
                                	],
                                	"id": 4,
                                	"imdb_id": "tt6443346",
                                	"original_language": "es",
                                	"original_title": "Black Adam",
                                	"overview": "Nearly 5,000 years after he was bestowed with the almighty powers of the Egyptian gods—and imprisoned just as quickly—Black Adam is freed from his earthly tomb, ready to unleash his unique form of justice on the modern world.",
                                	"popularity": 111,
                                	"release_date": "2022-10-19",
                                	"status": "Released",
                                	"tagline": "The world needed a hero. It got Black Adam.",
                                	"title": "Black Adam",
                                	"vote_average": 7.197,
                                	"vote_count": 4062
                                }
			""".trimIndent()
                    )
                )
        )
    }

    @AfterEach
    fun destroy() {
        peliculaRepository.deleteAll()
        generoRepository.deleteAll()

        wireMockServer.stop()
    }

    @Test
    fun `un usuario inexistente no puede consultar una película`() {
        // en este test no podemos utilizar GraphQLTester porque no prueba la capa de http (por ende
        // tampoco el filtro que obtiene el JWT)
        wireMockServer.stubFor(
            get("/auth/validate")
                .willReturn(
                    jsonResponse(
                        """
                    {
                    	"timestamp": "2023-02-09T02:51:00.571+00:00",
                    	"status": 500,
                    	"error": "Internal Server Error",
                    	"message": "JWT expired at 2022-12-22T02:08:49Z. Current time: 2023-02-09T02:51:00Z, a difference of 4236131569 milliseconds.  Allowed clock skew: 0 milliseconds.",
                    	"path": "/auth/validate"
                    }
                """.trimIndent(), HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
                )
                .withHost(
                    equalTo("localhost")
                )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    query {
                      peliculaPorIdTMDB(idTMDB: 505642) {
                        idTMDB
                        titulo
                        fechaSalida
                      }
                    }"
                """.trimIndent()
                )
                .header("Authorization", tokenUsuarioInexistente)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
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
            .matches { pelicula -> pelicula.titulo.equals("Black Adam") }
    }

    @Test
    fun `un usuario existente puede consultar las peliculas mas vistas`() {
        graphQlTester.document(
            """
			query {
			  masVistas {
                idTMDB
                titulo
                fechaSalida
                vistas
			  }
			}
		""".trimIndent()
        )
            .execute()
            .path("masVistas")
            .entityList(Pelicula::class.java)
            .matches<GraphQlTester.EntityList<Pelicula>> { peliculas -> peliculas[0].titulo == "Nueve reinas" }
    }

    @Test
    fun `un usuario existente puede calificar una película por única vez`() {
        usuarioService.authorize("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3MTUzNjk2MiwiZXhwIjoxNjcyNjE2OTYyLCJyb2xlcyI6IlJPTEVfQURNSU4ifQ.xnLiQFGRWQ_FwdILrWDqRtFPalKRZ4Tv0bCAKT6fqjgV7QDBEERqRYNJPxRzB-SEn-55LNPsfJjR4IGEQMKbgQ")

        graphQlTester.document(
            """
            mutation {
                calificarPelicula(calificacionPelicula: {
                    idTMDB: 1200, 
                    usuario: "user1", 
                    valoracion: 8
                }) {
                    idTMDB,
                    titulo,
                    calificacionPromedio,
                    fechaSalida
                }
            }
    		""".trimIndent()
        )
            .execute()
            .path("calificarPelicula")
            .entity(Pelicula::class.java)
            .matches { pelicula ->
                pelicula.calificacionPromedio == 8.0
            }
    }


    fun crearGenero(nombre: String) = generoRepository.save(Genero().apply {
        descripcion = "Comedia"
        idOriginal = 1
    })

}


data class CredencialesDTO(val usuario: String, val password: String)
