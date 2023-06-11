package org.uqbar.peliculamicroservicecontent.dto

class VerPeliculaDTO {
    var data: DataDTO? = null
    var file: String? = null
    var errors = mutableListOf<ErrorDTO>()

    fun isOk(): Boolean = errors.isEmpty()
}

class DataDTO {
    var verPelicula: PeliculaDTO? = null
}

class PeliculaDTO {
    var idTMDB: Int = 0
    var titulo: String = ""
    var vistas: Int = 0
}

class ErrorDTO {
    var message: String = ""
    var locations = mutableListOf<LocationDTO>()
    var extensions: ExtensionDTO? = null
}

class LocationDTO {
    var line: Int = 0
    var column: Int = 0
}

class ExtensionDTO {
    var classification: String = ""
}