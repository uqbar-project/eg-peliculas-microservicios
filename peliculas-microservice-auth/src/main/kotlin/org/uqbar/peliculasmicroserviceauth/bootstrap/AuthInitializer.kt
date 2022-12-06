package org.uqbar.peliculasmicroserviceauth.bootstrap

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.repository.UsuarioRepository

@Service
class AuthInitializer : InitializingBean {

   @Autowired
   lateinit var usuarioRepository: UsuarioRepository

   val logger = LoggerFactory.getLogger(AuthInitializer::class.java)

   override fun afterPropertiesSet() {
      logger.info("Inicializando usuarios")
      if (usuarioRepository.count() == 0L) {
         logger.info("Creando usuario admin")
         usuarioRepository.save(Usuario().apply {
            nombre = "admin"
            crearPassword("123456")
         })
      } else {
         logger.info("No hubo usuarios nuevos generados")
      }
   }
}