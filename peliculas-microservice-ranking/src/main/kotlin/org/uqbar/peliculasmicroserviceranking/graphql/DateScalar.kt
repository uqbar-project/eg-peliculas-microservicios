package org.uqbar.peliculasmicroserviceranking.graphql

import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val defaultFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Configuration
class DateScalarConfiguration {
   @Bean
   fun dateScalar(): GraphQLScalarType {
      return GraphQLScalarType.newScalar()
         .name("Date")
         .description("Tipo Date para GraphQL")
         .coercing(object : Coercing<LocalDate?, String?> {

               override fun serialize(dataFetcherResult: Any): String {
                  return (dataFetcherResult as? LocalDate)?.format(defaultFormatter)
                     ?: throw CoercingSerializeException("El objeto no es de tipo LocalDate: ${dataFetcherResult.javaClass.name}")
               }

               override fun parseValue(input: Any): LocalDate {
                  return LocalDate.parse(input.toString(), defaultFormatter)
               }

               override fun parseLiteral(input: Any): LocalDate {
                  return LocalDate.parse(input.toString(), defaultFormatter)
               }

         }).build()
   }

   @Bean
   fun configurer(): RuntimeWiringConfigurer {
      val scalarType: GraphQLScalarType = dateScalar()
      return RuntimeWiringConfigurer { builder -> builder.scalar(scalarType) }
   }
}
