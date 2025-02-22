package com.cmc.domain.base.exception

sealed class AppInternalException(message: String) : AppException(message) {
    class IOException(override val message: String, val data: Any? = null) : AppInternalException(message)
    class OperationCancelled(override val message: String, val data: Any? = null) : AppInternalException(message)
    class DatabaseError(override val message: String, val data: Any? = null) : AppInternalException(message)
    class PermissionDenied(override val message: String, val data: Any? = null) : AppInternalException(message)
    class UnknownError(override val message: String, val data: Any? = null) : AppInternalException(message)
}