package com.cmc.domain.base.exception

sealed class ApiException(message: String, data: Any? = null) : AppException(message) {
    class BadRequest(override val message: String) : ApiException(message)
    class Unauthorized(override val message: String) : ApiException(message)
    class NotFound(override val message: String) : ApiException(message)
    class AccessTokenExpired(override val message: String) : ApiException(message)
    class TokenExpired(override val message: String) : ApiException(message)
    class ServerError(override val message: String) : ApiException(message)
    class RegistrationLimitExceeded(override val message: String, val data: Any?) : ApiException(message)
}

class InvalidJsonFormatException(message: String): AppException(message)