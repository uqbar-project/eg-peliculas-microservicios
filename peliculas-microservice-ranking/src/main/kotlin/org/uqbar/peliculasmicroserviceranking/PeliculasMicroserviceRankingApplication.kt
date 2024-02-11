package org.uqbar.peliculasmicroserviceranking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

// es importante la configuración exclude para no sufrir el no suitable driver class
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class PeliculasMicroserviceRankingApplication

fun main(args: Array<String>) {
	runApplication<PeliculasMicroserviceRankingApplication>(*args)
}

