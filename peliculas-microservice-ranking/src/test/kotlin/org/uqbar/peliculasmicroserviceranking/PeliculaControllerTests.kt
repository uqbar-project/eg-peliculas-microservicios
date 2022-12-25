package org.uqbar.peliculasmicroserviceranking

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PeliculaControllerTests {

//	private val mapper = jacksonObjectMapper().apply {
//		registerModule(JavaTimeModule())
//	}

//	@Autowired
//	lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setup() {
		println("holi")
	}

	@Test
	fun `fake test`() {
		assertEquals(1, 1)
	}

}
