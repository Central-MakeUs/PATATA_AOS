package com.cmc.data.base


import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.base.exception.AppInternalException
import com.cmc.domain.base.exception.NetworkException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import kotlin.reflect.KClass

suspend fun <T : Any, R> apiRequestCatching(
    apiCall: suspend () -> ApiResponse<T>,
    transform: (T) -> R = { it as R },
    successCallBack: suspend (T) ->  Unit = {},
    responseClass: KClass<T>? = null,
): Result<R> {
    return runCatching {
        val response = apiCall()
        if (response.isSuccess && response.result != null) {
            successCallBack(response.result)
            Result.success(transform(response.result))
        } else if (response.isSuccess && responseClass == Unit::class) {
            successCallBack(Unit as T)
            Result.success(Unit as R)
        } else {
            val createException = createResultException(response)
            Result.failure(createException)
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

private fun <T> createResultException(response: ApiResponse<T>): ApiException {
    val errorCode = response.code
    val errorMessage = response.message
    val errorResult = response.result

    return when (errorCode) {
        ApiCode.Spot.SPOT_TOO_MANY_REGISTERED -> ApiException.RegistrationLimitExceeded(errorMessage, errorResult)
        else -> ApiException.ServerError("알 수 없는 오류 발생")
    }
}

private fun createException(errorBody: String?): Exception {
    val errorResponse = GsonProvider.gson.fromJson(errorBody, ErrorResponse::class.java)
    return mapErrorCodeToException(errorResponse.code, errorResponse.message)
}

private fun mapErrorCodeToException(errorCode: String, message: String, result: Any? = null): ApiException {
    return errorCodeMap[errorCode]?.invoke(message, result) ?: ApiException.ServerError(message)
}

private val errorCodeMap = mapOf(
    ApiCode.Auth.INVALID_GOOGLE_ID_TOKEN to { msg: String, _: Any? ->  ApiException.NotFound(msg) },
    ApiCode.Auth.GOOGLE_ID_TOKEN_VERIFICATION_FAILED to { msg: String, _: Any? ->  ApiException.Unauthorized(msg) },
    ApiCode.Common.GENERIC_ERROR to { msg: String, _: Any? ->  ApiException.BadRequest(msg) },
    ApiCode.Spot.SPOT_SEARCH_NO_RESULT to { msg: String, _: Any? ->  ApiException.NotFound(msg) },
    ApiCode.Spot.SPOT_TOO_MANY_REGISTERED to { msg: String, data: Any? ->  ApiException.RegistrationLimitExceeded(msg, data) },
)

fun <R> Result<R>.asFlow(): Flow<Result<R>> {
    return flow {
        emit(this@asFlow)
    }
}