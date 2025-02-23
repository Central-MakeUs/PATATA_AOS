package com.cmc.presentation.login.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.design.component.PatataAlert
import com.cmc.design.util.EaseOutBounceInterpolator
import com.cmc.design.util.SnackBarUtil
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding
import com.cmc.presentation.login.manager.GoogleSignInManager
import com.cmc.presentation.login.manager.LoginManager
import com.cmc.presentation.login.viewmodel.LoginSideEffect
import com.cmc.presentation.login.viewmodel.LoginState
import com.cmc.presentation.login.viewmodel.LoginViewModel
import com.cmc.presentation.util.CrashlyticsLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val loginManager = LoginManager()
    private val googleSignInManager by lazy { GoogleSignInManager(requireActivity()) }

    override fun initView() {
        runLoginAnimation()
        setLoginButton()
    }

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state  -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    private fun updateUI(state: LoginState) {
        if (state.loginSuccess) {
            state.user?.let { viewModel.handleLoginResult(it) }
        }
    }
    private fun handleSideEffect(effect: LoginSideEffect) {
        when (effect) {
            is LoginSideEffect.ShowSnackBar -> { showSnackBar(effect.message) }
            is LoginSideEffect.NavigateToHome -> { (activity as GlobalNavigation).navigateHome() }
            is LoginSideEffect.NavigateToProfileSetting -> { navigate(R.id.navigate_profile_input)}
        }
    }

    private fun showGoogleAccountRegistrationPrompt() {
        PatataAlert(requireContext())
            .title("구글 계정이 등록되어 있지 않습니다.")
            .content("Patata 앱에 로그인 하기 위해서는 구글 계정 등록이 필요합니다. 계정을 추가해주세요.")
            .multiButton {
                leftButton(getString(R.string.cancel)) { }
                rightButton(getString(R.string.confirm)) {
                    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    }
                    startActivity(intent)
                }
            }.show()
    }
    private val startForResult: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val credential = loginManager.oneTapClient.getSignInCredentialFromIntent(intent)
                    credential.googleIdToken?.let { idToken ->
                        repeatWhenUiStarted {
                            viewModel.googleLogin(idToken)
                        }
                    }
                }
            }
        }
    private fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.googleLogin(idToken)
                }
            } else {
                showGoogleAccountRegistrationPrompt()
            }
        } catch (e: ApiException) {
            CrashlyticsLogger.apply {
                setLastUIAction("구글 로그인 실패")
                log("로그인 실패: ${e.localizedMessage}")
            }
            showGoogleAccountRegistrationPrompt()
        }
    }
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            handleGoogleSignInResult(result.data)
        } else {
            showGoogleAccountRegistrationPrompt()
        }
    }
    private fun startGoogleSignIn() {
        repeatWhenUiStarted {
            runCatching {
                val signInIntent = googleSignInManager.getGoogleSignInIntent()
                googleSignInLauncher.launch(signInIntent)
            }.onFailure {
                CrashlyticsLogger.apply {
                    setLastUIAction("구글 로그인 버튼 클릭")
                    log("Google 로그인 실패: ${it.localizedMessage}")
                }
                showGoogleAccountRegistrationPrompt()
            }
        }
    }

    private fun setLoginButton() {
        binding.layoutGoogleLogin.setOnClickListener {
            startGoogleSignIn()
//            repeatWhenUiStarted {
//                try {
//                    val pendingIntent = loginManager.signInIntent(requireActivity())
//                    startForResult.launch(
//                        IntentSenderRequest.Builder(pendingIntent)
//                            .build()
//                    )
//                } catch (e: com.google.android.gms.common.api.ApiException) {
//                    e.stackTrace
//                    CrashlyticsLogger.apply {
//                        setLastUIAction("구글 로그인 버튼 클릭")
//                        log("$e")
//                    }
//                    showGoogleAccountRegistrationPrompt()
//                } catch (e: Exception) {
//                    FirebaseCrashlytics.getInstance().recordException(e)
//                    e.stackTrace
//                }
//            }
        }
    }

    private fun runLoginAnimation() {
        repeatWhenUiStarted {
            launch {
                while (true) {
                    delay(2000)
                    animateViewBounce(binding.ivAnimationPrinter)
                    clearAnimationEffect(binding.ivAnimationPolaroid)
                    delay(700)
                    animateViewDownWithVisible(binding.ivAnimationPolaroid)
                    delay(550)
                    animateViewDownWithGone(binding.ivAnimationPolaroid)
                }
            }
        }
    }

    private fun animateViewBounce(view: View) {
        view.isVisible = true

        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.7f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.7f, 1.2f, 1f)

        scaleX.duration = 300
        scaleY.duration = 300

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }
    private fun clearAnimationEffect(view: View) {
        view.translationY = 0f
        view.isVisible = true
        view.alpha = 0.8f
    }
    private fun animateViewDownWithVisible(view: View) {
        val distance = view.height.toFloat()

        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, distance).apply {
            duration = 500
            interpolator = EaseOutBounceInterpolator()
            start()
        }
    }
    private fun animateViewDownWithGone(view: View) {
        val distance = view.height.toFloat()

        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, view.translationY + (distance * 0.8).toInt()).apply {
            duration = 300
            interpolator = AnticipateInterpolator()
            doOnEnd { view.isVisible = false }
            start()
        }
    }

    private fun showSnackBar(message: String) { SnackBarUtil.show(binding.root, message) }
}