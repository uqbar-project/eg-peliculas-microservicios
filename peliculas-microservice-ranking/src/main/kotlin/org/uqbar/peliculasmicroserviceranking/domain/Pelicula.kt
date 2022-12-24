package org.uqbar.peliculasmicroserviceranking.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "pelicula")
class Pelicula {
   @Id
   var id: String? = null

   lateinit var idTMDB: Number
   lateinit var titulo: String

   var generos: MutableList<Genero> = mutableListOf()
   lateinit var idioma: String
   lateinit var sinopsis: String
   lateinit var fechaSalida: LocalDate
   var calificacionPromedio: Number = 0

   var vistas = 0

   var calificaciones = mutableListOf<Calificacion>()

   fun sumarVista() {
      vistas++
   }

   fun calificar(_usuario: Usuario, _calificacion: Int) {
      calificaciones.add(Calificacion().apply {
         usuario = _usuario
         valoracion = _calificacion
      })
      actualizarCalificacionPromedio()
   }

   fun actualizarCalificacionPromedio() {
      calificacionPromedio = calificaciones.map { it.valoracion.toDouble() }.average()
   }

   fun calificadaPor(usuario: Usuario) = calificaciones.any { it.usuario.nombre == usuario.nombre }

}
