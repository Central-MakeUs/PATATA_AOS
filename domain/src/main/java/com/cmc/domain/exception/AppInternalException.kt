package com.cmc.domain.exception

sealed class AppInternalException(message: String) : AppException(message) {
    object OperationCancelled : AppInternalException("The operation was cancelled by the user.")
    object DatabaseError : AppInternalException("Database error occurred")
    object PermissionDenied : AppInternalException("Permission denied")
    object UnknownError : AppInternalException("Unknown error occurred")
}