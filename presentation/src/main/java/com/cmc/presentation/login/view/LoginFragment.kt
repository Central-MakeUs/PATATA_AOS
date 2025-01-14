package com.cmc.presentation.login.view

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding
import com.cmc.presentation.login.LoginManager
import com.cmc.presentation.login.viewmodel.LoginViewModel
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val loginManager = LoginManager()

    override fun initView() {
        binding.btnNext.setOnClickListener {
            (activity as GlobalNavigation).navigateHome()
        }
        setLoginButton()
    }

    private fun setLoginButton() {
        binding.btnGoogleLogin.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
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
        lifecycleScope.launch {
            viewModel.userState.collect {
                binding.tvLogin.text = it?.nickName ?: "Null"
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
                        lifecycleScope.launch {
                            viewModel.googleLogin(idToken)
                        }
                    }
                }
            }
        }
}