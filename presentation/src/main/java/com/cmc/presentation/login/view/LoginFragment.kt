package com.cmc.presentation.login.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.domain.exception.ApiException
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding
import com.cmc.presentation.login.LoginManager
import com.cmc.presentation.login.viewmodel.LoginState
import com.cmc.presentation.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val loginManager = LoginManager()

    override fun initView() {
        loginAnimationStart()
        setLoginButton()

    }

    private fun setLoginButton() {
        binding.layoutGoogleLogin.setOnClickListener {
            findNavController().navigate(R.id.navigate_profile_setting)
        }
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


    override fun initObserving() {
        repeatWhenUiStarted {
            viewModel.state.collect { state  ->
                when (state) {
                    is LoginState.Success -> {
                        withContext(Dispatchers.Main) {
                            state.user.nickName?.let {
                                (activity as GlobalNavigation).navigateHome()
                            } ?: run {
                                findNavController().navigate(R.id.navigate_profile_setting)
                            }
                        }
                    }
                    else -> {}
                }
            }
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
                            val name = viewModel.googleLogin(idToken)
                        }
                    }
                }
            }
        }


    private fun loginAnimationStart() {
        repeatWhenUiStarted {
            repeat(5) {
                delay(2000)
                animateViewBounce(binding.ivAnimationPrinter)
                clearAnimationEffect(binding.ivAnimationPolaroid)
                delay(300)
                animateViewDown(binding.ivAnimationPolaroid)
                delay(300)
                animateViewDownWithGone(binding.ivAnimationPolaroid)
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
        view.alpha = 1f
    }

    private fun animateViewDown(view: View) {
        val distance = view.height.toFloat()

        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, distance).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
            start()
        }
    }

    private fun animateViewDownWithGone(view: View) {
        val distance = view.height.toFloat()

        val moveView = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, view.translationY + distance).apply {
            duration = 200
            interpolator = LinearInterpolator()
        }
        val changeAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
            duration = 200
            interpolator = LinearInterpolator()
        }

        AnimatorSet().apply {
            playTogether(moveView, changeAlpha)
            start()
        }
    }

}