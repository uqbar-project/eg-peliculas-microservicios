
# Ejemplo Microservicios - Películas

## Módulo de Contenido (content)

[![build-auth](https://github.com/uqbar-project/eg-peliculas-microservicios/actions/workflows/build-content.yml/badge.svg)](https://github.com/uqbar-project/eg-peliculas-microservicios/actions/workflows/build-content.yml) [![codecov](https://codecov.io/gh/uqbar-project/eg-peliculas-microservicios/branch/master/graph/badge.svg?token=VGXucKfbqn&flag=content)](https://codecov.io/gh/uqbar-project/eg-peliculas-microservicios)


## Instalación

```bash
cache_1  | 1:M 02 Feb 2023 02:40:55.825 # WARNING Memory overcommit must be enabled! Without it, a background save or replication may fail under low memory condition. Being disabled, it can can also cause failures without low memory condition, see https://github.com/jemalloc/jemalloc/issues/1328. To fix this issue add 'vm.overcommit_memory = 1' to /etc/sysctl.conf and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.
```

```bash
docker-compose up
```

Eso levanta tanto el servidor Redis como el Redis Commander.
