package org.uqbar.peliculasmicroserviceregistry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class PeliculasMicroserviceRegistryApplication

fun main(args: Array<String>) {
	runApplication<PeliculasMicroserviceRegistryApplication>(*args)
}
