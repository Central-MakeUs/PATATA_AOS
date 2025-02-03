package com.cmc.domain.base.exception

sealed class ApiException(message: String) : AppException(message) {
    class BadRequest(override val message: String) : ApiException(message)
    class Unauthorized(override val message: String) : ApiException(message)
    class NotFound(override val message: String) : ApiException(message)
    class AccessTokenExpired(override val message: String) : ApiException(message)
    class TokenExpired(override val message: String) : ApiException(message)
    class ServerError(override val message: String) : ApiException(message)
}