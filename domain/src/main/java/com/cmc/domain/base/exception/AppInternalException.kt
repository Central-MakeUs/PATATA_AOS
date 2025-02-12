package com.cmc.domain.base.exception

sealed class AppInternalException(message: String) : AppException(message) {
    class IOException(override val message: String) : AppInternalException(message)
    data object OperationCancelled : AppInternalException("The operation was cancelled by the user.")
    data object DatabaseError : AppInternalException("Database error occurred")
    data object PermissionDenied : AppInternalException("Permission denied")
    data object UnknownError : AppInternalException("Unknown error occurred")
}