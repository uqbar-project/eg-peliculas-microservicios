package org.uqbar.peliculamicroservicecontent.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.uqbar.peliculamicroservicecontent.dto.VerPeliculaDTO
import org.uqbar.peliculamicroservicecontent.model.Content
import org.uqbar.peliculamicroservicecontent.repository.ContentRepository
import kotlin.jvm.optionals.getOrElse


@Service
@Transactional
class ContentService {

    val logger: Logger = LoggerFactory.getLogger(ContentService::class.java)

    @Autowired
    lateinit var contentRepository: ContentRepository

    @Autowired
    lateinit var usuarioService: UsuarioService

    @Value("\${ranking.base-url}")
    lateinit var rankingBaseUrl: String

    fun watchContent(idTMDB: String): VerPeliculaDTO {
        // Avisamos que una película se vio
        // https://stackoverflow.com/questions/70519410/how-to-invoke-graphql-api-from-a-java-spring-boot-application-is-there-any-anno
        // https://medium.com/decathlontechnology/minimal-graphql-client-request-with-spring-boot-22e0041b170
        val token = usuarioService.token

        logger.info("Token $token")

        val webClient = WebClient
            .builder()
            .build()

        val graphqlMutationBody = """mutation {
                verPelicula(idTMDB: $idTMDB) {
                    idTMDB,
                    titulo,
                    vistas
                }
            }
        """.trimIndent()
        logger.info("Body $graphqlMutationBody")

        val verPeliculaDTO = webClient.post()
            .uri(rankingBaseUrl)
            .headers { header -> header.setBearerAuth(token) }
            .bodyValue(GraphQLRequest(graphqlMutationBody))
            .retrieve()
            .bodyToMono(VerPeliculaDTO::class.java)
            .block()!!

        if (verPeliculaDTO.isOk()) {
            logger.info("Buscando película $idTMDB")
            val contentBD = contentRepository.findById(idTMDB).getOrElse {
                logger.info("Nuevo content - Película ($idTMDB)")
                contentRepository.save(Content().apply {
                    id = idTMDB
                    file = getContent(idTMDB)
                })
            }
            verPeliculaDTO.file = contentBD.file
        }

        return verPeliculaDTO
    }

    /** Simula recuperar contenido que podría estar en un archivo dentro de este servidor */
    private fun getContent(idContent: String): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..4096)
            .map { allowedChars.random() }
            .joinToString("")
    }
}

data class GraphQLRequest(
    var query: String? = null
)