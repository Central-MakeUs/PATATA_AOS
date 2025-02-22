package com.cmc.data.base


import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.base.exception.AppException
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
        exception.printStackTrace()
        when (exception) {
            is IOException -> Result.failure(NetworkException.NoInternetConnection)
            is HttpException ->  {
                Result.failure(createException(exception.response()?.errorBody()?.string()))
            }
            else -> Result.failure(AppInternalException.UnknownError(exception.message ?: "알 수 없는 오류 발생"))
        }
    }
}

private fun <T> createResultException(response: ApiResponse<T>): AppException {
    val errorCode = response.code
    val errorMessage = response.message
    val errorResult = response.result

    return when (errorCode) {
        ApiCode.Spot.SPOT_TOO_MANY_REGISTERED -> ApiException.RegistrationLimitExceeded(errorMessage, errorResult)
        else -> AppInternalException.UnknownError("알 수 없는 오류 발생")
    }
}

private fun createException(errorBody: String?): Exception {
    val exception = try {
        val errorResponse = GsonProvider.gson.fromJson(errorBody, ErrorResponse::class.java)
        mapErrorCodeToException(errorResponse.code, errorResponse.message)
    } catch (e: Exception) {
        e.printStackTrace()
        AppInternalException.IOException(e.message ?: "CreateException GsonProvider Error")
    }
    return exception
}

private fun mapErrorCodeToException(errorCode: String, message: String, result: Any? = null): AppException {
    return errorCodeMap[errorCode]?.invoke(message, result) ?: ApiException.ServerError(message)
}

fun <R> Result<R>.asFlow(): Flow<Result<R>> {
    return flow {
        emit(this@asFlow)
    }
}