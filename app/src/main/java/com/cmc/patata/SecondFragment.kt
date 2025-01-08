package com.cmc.patata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmc.patata.databinding.FragmentSecondBinding
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val loginManager = LoginManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        setLoginButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLoginButton() {
        binding.googleLoginBtnTemplate.onDelayedClick {

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
//            showGoogleAccountRegistrationPrompt()

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
                    credential.googleIdToken?.let {
                        // TODO : Login 성공 API
                    }
                }
            }
        }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}