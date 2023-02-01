package org.uqbar.peliculasmicroserviceauth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BusinessException(msg: String) : RuntimeException(msg)
