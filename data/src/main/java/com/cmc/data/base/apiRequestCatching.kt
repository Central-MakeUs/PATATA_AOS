package com.cmc.data.base

import com.cmc.domain.exception.ApiException
import com.cmc.domain.exception.AppInternalException
import com.cmc.domain.exception.NetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

suspend fun <T, R> apiRequestCatching(
    apiCall: suspend () -> ApiResponse<T>,
    transform: (T) -> R,
    successCallBack: suspend (T) ->  Unit = {},
): Result<R> {
    return runCatching {
        val response = apiCall()
        if (response.isSuccess && response.result != null) {
            successCallBack(response.result)
            Result.success(transform(response.result))
        } else {
            Result.failure(ApiException.ServerError(response.message))
        }
    }.getOrElse { exception ->
        when (exception) {
            is IOException -> Result.failure(NetworkException.NoInternetConnection)
            is HttpException ->  {
                Result.failure(createException(exception.response()?.errorBody()?.string()))
            }
            else -> Result.failure(AppInternalException.UnknownError)
        }
    }
}

private fun createException(errorBody: String?): Exception {
    val errorResponse = GsonProvider.gson.fromJson(errorBody, ErrorResponse::class.java)
    val createExceptionFunc = errorCodeMap[errorResponse.code] ?: { msg: String -> ApiException.ServerError(msg) }
    return createExceptionFunc(errorResponse.message)
}

private val errorCodeMap = mapOf(
    ApiCode.Auth.INVALID_GOOGLE_ID_TOKEN to { msg: String -> ApiException.NotFound(msg) },
    ApiCode.Auth.GOOGLE_ID_TOKEN_VERIFICATION_FAILED to { msg: String -> ApiException.Unauthorized(msg) },
    ApiCode.Common.GENERIC_ERROR to { msg: String -> ApiException.BadRequest(msg) }
)
fun <R> Result<R>.asFlow(): Flow<Result<R>> {
    return flow {
        emit(this@asFlow)
    }
}