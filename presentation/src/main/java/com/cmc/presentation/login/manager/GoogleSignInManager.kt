package com.cmc.presentation.login.manager

import android.app.Activity
import android.content.Intent
import com.cmc.presentation.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.suspendCancellableCoroutine

class GoogleSignInManager(private val activity: Activity) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.my_web_client_id))
                .requestEmail()
                .build()
        )
    }

    suspend fun getGoogleSignInIntent(): Intent =
        suspendCancellableCoroutine { cancellableContinuation ->
            val signInIntent = googleSignInClient.signInIntent
            cancellableContinuation.resume(signInIntent, null)
        }
}