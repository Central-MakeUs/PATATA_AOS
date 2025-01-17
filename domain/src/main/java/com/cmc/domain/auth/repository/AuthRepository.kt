package com.cmc.domain.auth.repository

import com.cmc.domain.auth.model.User

interface AuthRepository {

    suspend fun login(idToken: String): User

}