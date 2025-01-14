package com.cmc.data.auth.repository

import com.cmc.data.auth.mapper.toDomain
import com.cmc.data.auth.model.LoginRequest
import com.cmc.data.auth.remote.AuthApiService
import com.cmc.domain.auth.model.User
import com.cmc.domain.auth.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
): AuthRepository {

    override suspend fun login(idToken: String): User {
        return try {
            authApiService.googleLogin(
                LoginRequest(idToken)
            ).toDomain()
        } catch (e: HttpException) {
            e.printStackTrace()
            throw Exception("Http Remote 에러 발생")
        } catch (e : Exception) {
            e.printStackTrace()
            throw Exception("알 수 없는 에러가 발생했습니다.")
        }
    }

}