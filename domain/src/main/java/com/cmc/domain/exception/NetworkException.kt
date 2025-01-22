package com.cmc.domain.exception

sealed class NetworkException(message: String) : AppException(message) {
    object NoInternetConnection : NetworkException("No internet connection")
    object Timeout : NetworkException("Request timed out")
    object ServerUnavailable : NetworkException("Server is not available")
}