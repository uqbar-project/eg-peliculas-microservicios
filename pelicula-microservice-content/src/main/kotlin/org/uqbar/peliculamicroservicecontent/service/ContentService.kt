package org.uqbar.peliculamicroservicecontent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import org.uqbar.peliculamicroservicecontent.dto.VerPeliculaDTO
import org.uqbar.peliculamicroservicecontent.graphql.GraphqlMutationBody
import org.uqbar.peliculamicroservicecontent.graphql.GraphqlSchemaReaderUtil
import org.uqbar.peliculamicroservicecontent.model.Content
import org.uqbar.peliculamicroservicecontent.repository.ContentRepository
import kotlin.jvm.optionals.getOrElse


@Service
@Transactional
class ContentService {

    @Autowired
    lateinit var contentRepository: ContentRepository

    @Value("\${ranking.base-url}")
    lateinit var rankingBaseUrl: String

    private fun withBearerAuth(
        request: ClientRequest,
        token: String
    ): ClientRequest {
        return ClientRequest.from(request)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .build()
    }

    fun watchContent(idContent: String, token: String): VerPeliculaDTO {
        val content = contentRepository.findById(idContent).getOrElse {
            contentRepository.save(Content().apply {
                id = idContent
                file = getContent(idContent)
            })
        }
        // Avisamos que una película se vio
        // https://stackoverflow.com/questions/70519410/how-to-invoke-graphql-api-from-a-java-spring-boot-application-is-there-any-anno

        // https://medium.com/decathlontechnology/minimal-graphql-client-request-with-spring-boot-22e0041b170

        println("Token $token")

        val webClient = WebClient
            .builder()
            .build()

        val verPeliculasVariables: String = GraphqlSchemaReaderUtil.getSchemaFromFileName("variables")
        verPeliculasVariables.replace("idTMDB", idContent)

        val graphqlMutationBody = GraphqlMutationBody().apply {
            mutation = GraphqlSchemaReaderUtil.getSchemaFromFileName("verPeliculas")
            variables = verPeliculasVariables
        }

        val verPeliculaDTO = webClient.post()
            .uri(rankingBaseUrl)
            .headers({ header -> header.setBearerAuth(token)})
            .bodyValue(graphqlMutationBody)
            .retrieve()
            .bodyToMono(VerPeliculaDTO::class.java)
            .block()!!

//        val authRequest = RequestEntity.get("${baseUrl}/auth/users/$nombreUsuario")
//            .headers(HttpHeaders().apply {
//                // Asume que el token tiene que existir
//                setBearerAuth(token)
//            })
//            .build()
        //

        verPeliculaDTO.file = content.file

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