
# Ejemplo Microservicios - Películas

## Módulo de autenticación

## Instrucciones para levantar el ejemplo

Desde el directorio raíz en un shell se levanta la base de datos PostgreSQL con docker compose:

```bash
docker-compose up
```

En el archivo [`docker-compose.yml`](./docker-compose.yml) especificamos la imagen dockerizada de Postgres que queremos utilizar y adicionalmente tenemos un [script de inicialización](./Docker/init_db.sh) que crea la base de datos `peliculas_auth` y le da accesos a un usuario que creamos también.

## El modelo de datos

Tenemos como entidad principal al usuario, la password se guarda encriptada en la base y tiene asociada la información de facturas (se factura en una fecha particular por un monto, y se puede pagar solo el total). Un usuario tiene muchas facturas.

## Spring Security

Primera versión funciona correctamente, endpoint de login devuelve ok y el resto usa basic auth:

- el endpoint de login debería devolver un JWT
- y luego ese JWT deberíamos pasarlo en el header para validar el usuario en el resto de los endpoints

No está funcionando ni la autenticación básica (Basic Auth) en Insomnia.

Tenemos la configuración global en `application.yml`:

```yml
spring:
  # seguridad
  security:
    user:
      name: admin
      password: admin
```

## Cómo testear la aplicación

Levantar en Insomnia los endpoints importando [este archivo](./auth_insomnia.json), donde podés

- crear un usuario
- facturar a un usuario (con su id)
- pagar una factura con el id de usuario y de factura
- ver la información de los usuarios
- o de un usuario en particular
- y adicionalmente borrar un usuario

