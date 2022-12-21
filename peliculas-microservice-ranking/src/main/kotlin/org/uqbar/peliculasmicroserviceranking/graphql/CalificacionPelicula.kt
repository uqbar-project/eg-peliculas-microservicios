package org.uqbar.peliculasmicroserviceranking.graphql

data class CalificacionPelicula(
   val idTMDB: Int,
   val usuario: String,
   val valoracion: Int
)