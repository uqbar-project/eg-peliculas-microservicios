package org.uqbar.peliculamicroservicecontent.dto

class VerPeliculaDTO {
    var data: DataDTO? = null
    var file: String? = null
}

class DataDTO {
    var verPelicula: PeliculaDTO? = null
}

class PeliculaDTO {
    var idTMDB: Int = 0
    var titulo: String = ""
    var vistas: Int = 0
}