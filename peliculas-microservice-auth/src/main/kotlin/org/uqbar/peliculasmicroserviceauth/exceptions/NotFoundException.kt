package org.uqbar.peliculasmicroserviceauth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(msg: String) : RuntimeException(msg)