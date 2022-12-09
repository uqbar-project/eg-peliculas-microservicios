
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

## Endpoints

¿Qué podemos hacer dentro de este módulo?

- crear un usuario
- facturar a un usuario (con su id)
- pagar una factura con el id de usuario y de factura
- ver la información de los usuarios
- o de un usuario en particular
- y adicionalmente dar de baja un usuario (es una baja lógica, seguirá estando en la base)

## Mecanismos para securizar los endpoints

Este ejemplo utiliza Spring Security como tecnología de

- **autenticación:** la forma en que validamos que las credenciales de un usuario son correctas (una forma es mediante un nombre de usuario y su contraseña)
- **autorización:** verificando que cada usuario tenga permitido el uso de cada uno de los endpoints.

Existen múltiples frameworks que nos ayudan a completar este punto, pueden ver

- InMemoryUserDetailsManager: genera una base de usuarios en memoria cada vez que se levanta el servidor
- OAuth2: te podés integrar con servicios de autenticación externos, como tu correo de Google, Facebook, Github, etc.
- [Keycloak](https://www.keycloak.org/): provee un mecanismo de Single Sign On (SSO, o login unificado), federación de usuarios (cómo un usuario puede utilizarse en varios servidores sin tener que volver a generarlos)
- Basic Auth
- JWT (JSON Web Token), es la variante que nosotros decidimos implementar.

### JWT

[JWT](https://developer.okta.com/blog/2018/06/20/what-happens-if-your-jwt-is-stolen) representa 

- un token, un valor que almacena 
- información sobre la sesión asociada a un usuario identificado
- con formato JSON
- y firmado digitalmente, donde un algoritmo utiliza una clave como forma de encriptar los datos.

El estándar RFC 7519 define [JWT](https://jwt.io/) como un método que permite conectar dos partes, en nuestro caso un cliente y un servidor. El cliente puede ser cualquier aplicación construida con un frontend, o en este caso, dispararse vía POSTMAN / Insomnia.

Los pasos son

- el cliente pasa sus credenciales, en este caso usuario y contraseña (otro medio podría ser tener una [API Key](https://www.fortinet.com/resources/cyberglossary/api-key))
- el servidor valida el login: en caso exitoso le devuelve un token que contiene información como el usuario y el momento en que vence ese token. La información está encriptada utilizando algún algoritmo que el cliente desconoce.
- el cliente utilizará de aquí en más ese token para hacer los pedidos siguientes, de manera de no tener que pasar la contraseña cada vez que quiera ejecutar un endpoint

Esta forma de trabajo es especialmente útil para el protocolo http/s que es _stateless_, el servidor no guarda información de sesión. Entonces es responsabilidad del cliente almacenar el token y enviarlo en cada pedido, para que el server pueda identificar al usuario que realiza ese pedido.

![flujo JWT](./images/jwt.jpeg)

## Login

El endpoint de login necesita recibir la información del usuario y la contraseña. Dado que todavía no nos identificamos, necesitamos que este endpoint no tenga ningún requerimiento de seguridad, debemos habilitarlo para que cualquier usuario anónimo verifique su identidad.

```kotlin
@Configuration
@EnableWebSecurity
class WebSecurityConfig {
   ...

   @Bean
   fun filterChain(httpSecurity: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
      return httpSecurity
         .cors().disable()
         .csrf().disable()
         .authorizeHttpRequests()
         .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
         .requestMatchers("/error").permitAll()
         ...
         .and()
         .build()
```

> Un detalle importante de mencionar es que **también hay que habilitar la ruta `/error`**, de lo contrario cada vez que lancemos una excepción, en lugar de devolvernos un código de http 400, 404, etc. estaremos recibiendo un 401 ó 403 que nos dirá que no tenemos acceso a la ruta que tiene configurada Springboot para mostrar un mensaje de error.


### 

## Cómo testear la aplicación

Levantar en Insomnia los endpoints importando [este archivo](./auth_insomnia.json).

