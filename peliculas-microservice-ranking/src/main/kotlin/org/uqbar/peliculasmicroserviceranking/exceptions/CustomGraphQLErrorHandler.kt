package org.uqbar.peliculasmicroserviceranking.exceptions

import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
   companion object {
      private val log: Logger = LoggerFactory.getLogger(this::class.java)
   }

   override fun resolveToSingleError(e: Throwable, env: DataFetchingEnvironment): GraphQLError? {
      log.warn("Exception while handling request: ${e.message}", e)
      return when (e) {
         is BusinessException -> toGraphQLError(e.message, ErrorType.ValidationError)
         is HttpClientErrorException -> toGraphQLError(e.getResponseBodyAs(RuntimeException::class.java)?.message)
         is Exception -> toGraphQLError(e.message)
         else -> super.resolveToSingleError(e, env)
      }
   }

   private fun toGraphQLError(message: String?, errorType: ErrorType = ErrorType.DataFetchingException): GraphQLError? {
      return GraphqlErrorBuilder.newError().message(message ?: "Error interno").errorType(errorType).build()
   }

}