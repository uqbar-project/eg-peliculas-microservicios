package org.uqbar.peliculamicroservicecontent

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.JvmProxyConfigurer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests de Content")
class ContentControllerTests {

    val token = "Bearer tokencito"

    private val mapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(
            WireMockConfiguration.options()
                .port(9080)
                .enableBrowserProxying(true)
        )
        wireMockServer.start()

        JvmProxyConfigurer.configureFor(wireMockServer)
    }

    @AfterEach
    fun destroy() {
        wireMockServer.stop()
    }

    // region watch
    @Test
    fun `usuario inexistente no puede ver una película`() {
        stubTokenInexistente()
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/content/watch")
                .header("Authorization", token)
                .content("1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `usuario existente puede ver una película`() {
        stubTokenExistente()
        stubPeliculaOk()
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/content/watch")
                .header("Authorization", token)
                .content("1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.verPelicula.idTMDB").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.verPelicula.titulo").value("Esperando la carroza"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.verPelicula.vistas").value(522.toString()))
    }
    //

    private fun stubTokenInexistente() {
        wireMockServer.stubFor(
            WireMock.get("/auth/validate")
                .willReturn(
                    WireMock.jsonResponse(
                        """
                    {
                    	"timestamp": "2023-02-09T02:51:00.571+00:00",
                    	"status": 500,
                    	"error": "Internal Server Error",
                    	"message": "JWT expired at 2022-12-22T02:08:49Z. Current time: 2023-02-09T02:51:00Z, a difference of 4236131569 milliseconds.  Allowed clock skew: 0 milliseconds.",
                    	"path": "/auth/validate"
                    }
                """.trimIndent(), HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
                .withHost(
                    WireMock.equalTo("localhost")
                )
        )
    }

    private fun stubTokenExistente() {
        wireMockServer.stubFor(
            WireMock.get("/auth/validate")
                .willReturn(WireMock.ok("ok"))
                .withHost(
                    WireMock.equalTo("localhost")
                )
        )
    }

    private fun stubPeliculaOk() {
        wireMockServer.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.jsonResponse("""
                    {
                    	"data": {
                    		"verPelicula": {
                    			"idTMDB": 1,
                    			"titulo": "Esperando la carroza",
                    			"vistas": 522
                    		}
                    	}
                    }
                """.trimIndent(), HttpStatus.OK.value()))
        )
    }
}

