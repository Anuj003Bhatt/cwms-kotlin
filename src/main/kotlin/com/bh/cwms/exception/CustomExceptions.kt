package com.bh.cwms.exception

import org.slf4j.helpers.MessageFormatter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class CwmsException : RuntimeException {
    constructor(): super()
    constructor(message: String) : super(message)
    constructor(message: String, vararg args: Any) : super(MessageFormatter.arrayFormat(message, args).message)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException : CwmsException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, vararg args: Any) : super(message, args)
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException : CwmsException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, vararg args: Any) : super(message, args)
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class AuthenticationFailedException : CwmsException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, vararg args: Any) : super(message, args)
}