package org.uqbar.peliculasmicroserviceranking.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ResultDTO {
   lateinit var id: Number
   lateinit var title: String
   lateinit var overview: String
   lateinit var original_language: String
   lateinit var release_date: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MovieDTO {
   lateinit var page: Number
   lateinit var results: List<ResultDTO>
}
