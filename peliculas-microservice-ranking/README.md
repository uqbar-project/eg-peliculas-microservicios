
# Módulo Ránking de películas

Este módulo permite

- visualizar información de las películas
- indicar que se ve una determinada película
- calificar una película
- hacer búsquedas de una película

# Conceptos salientes

- Medio persistente: base de datos documental creada desde Docker
- Mapeo OD/M con Spring Boot
- El repositorio es reactivo: ReactiveCrudRepository, esto implica que los queries y las actualizaciones son asincrónicas
- Los endpoints están manejados con GraphQL, eso permite manipular la información que se quiera de las películas
- La información de las películas se obtiene de [TMDB (The Movie Database)](https://www.themoviedb.org/), para lo cual tenés que pedir una API Key propia y ubicarla dentro del archivo `application-localhost.yml` del raíz de este proyecto

```yml
tmdb:
  api-key: <tu API key>

```

Dado que es información sensible, **no la subimos a git** (está ignorado en el archivo `.gitignore` raíz del monorepo). Esta es una práctica recomendable para no compartir claves, especialmente en un lugar tan público como github / gitlab.

## Tutoriales

- [Reactive Spring Boot](https://www.youtube.com/watch?v=IK26KdGRl48&list=PLnXn1AViWyL70R5GuXt_nIDZytYBnvBdd&ab_channel=CodeWithDilip_
