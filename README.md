
# Monorepo Ejemplo de Películas - Microservicios en Springboot

Esta es la página principal del ejemplo de Películas construido a partir de una serie de microservicios en Springboot. Podés ver a continuación

- [**auth**: el módulo de autenticación y facturación](./peliculas-microservice-auth/README.md)
- [**ranking**: el catálogo de las películas con el ránking propio y las estadísticas de visualización](./peliculas-microservice-ranking/README.md)
- [**content**: el servicio que permite acceder al contenido almacenado en caches e informar visualizaciones](./pelicula-microservice-content/README.md)
- [**registry**: el server que funciona como registro de los microservicios](./peliculas-microservice-registry/README.md)

La arquitectura general podemos verla en este diagrama de alto nivel:

![diagrama microservicios](images/Microservicios%20Peliculas.drawio.png)

