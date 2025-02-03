package com.cmc.presentation.login.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.design.util.ExponentialAccelerateInterpolator
import com.cmc.domain.base.exception.ApiException
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding
import com.cmc.presentation.login.LoginManager
import com.cmc.presentation.login.viewmodel.LoginSideEffect
import com.cmc.presentation.login.viewmodel.LoginState
import com.cmc.presentation.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val loginManager = LoginManager()

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
        when (state) {
            is LoginState.Success -> {
                viewModel.handleLoginResult(state.user)
            }
            else -> {}
        }
    }
    private fun handleSideEffect(effect: LoginSideEffect) {
        when (effect) {
            is LoginSideEffect.NavigateToHome -> { (activity as GlobalNavigation).navigateHome() }
            is LoginSideEffect.NavigateToProfileSetting -> { navigate(R.id.navigate_profile_setting)}
        }
    }

    private fun showGoogleAccountRegistrationPrompt() {
        Toast.makeText(requireContext(), "구글 계정을 등록해주세요.", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        startActivity(intent)
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


    private fun setLoginButton() {
        binding.layoutGoogleLogin.setOnClickListener {
            repeatWhenUiStarted {
                try {
                    startForResult.launch(
                        IntentSenderRequest.Builder(loginManager.signInIntent(requireActivity()))
                            .build()
                    )
                } catch (e: ApiException) {
                    Log.e("Login", "ApiException $e")
                    showGoogleAccountRegistrationPrompt()
                } catch (e: Exception) {
                    Log.e("Login", "setLoginButton Exception $e")
                }
            }
        }
    }

    private fun runLoginAnimation() {
        repeatWhenUiStarted {
            launch {
                while (true) {
                    delay(2000)
                    animateViewBounce(binding.ivAnimationPrinter)
                    clearAnimationEffect(binding.ivAnimationPolaroid)
                    delay(300)
                    animateViewDownWithVisible(binding.ivAnimationPolaroid)
                    delay(880)
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
        view.alpha = 0.6f
    }
    private fun animateViewDownWithVisible(view: View) {
        val distance = view.height.toFloat()

        val moveView = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, distance).apply {
            duration = 800
            interpolator = ExponentialAccelerateInterpolator()
        }

        val changeAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            duration = 600
            interpolator = AccelerateInterpolator()
        }

        playTogetherAnimation(moveView, changeAlpha)
    }
    private fun animateViewDownWithGone(view: View) {
        val distance = view.height.toFloat()

        val moveView = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, view.translationY + distance).apply {
            duration = 300
            interpolator = AnticipateInterpolator()
        }
        val changeAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
            duration = 300
            interpolator = AccelerateInterpolator ()
        }
        playTogetherAnimation(moveView, changeAlpha)
    }
    private fun playTogetherAnimation(a1: ObjectAnimator, a2: ObjectAnimator) {
        AnimatorSet().apply {
            playTogether(a1, a2)
            start()
        }
    }
}