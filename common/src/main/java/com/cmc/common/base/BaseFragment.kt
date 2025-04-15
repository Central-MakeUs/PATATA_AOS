package com.cmc.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cmc.common.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseFragment<T: ViewDataBinding>(@LayoutRes val layoutRes: Int)
    : Fragment() {
    private var _binding: T? = null
    val binding get() = _binding!!

    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = this@BaseFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserving()
        observeNetworkStatus()
    }

    override fun onStart() {
        super.onStart()
        networkManager.registerNetworkCallback()
    }

    override fun onStop() {
        super.onStop()
        networkManager.unregisterNetworkCallback()
    }

    abstract fun initObserving()

    abstract fun initView()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun LifecycleOwner.repeatWhenUiStarted(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }

    /**
     * 프래그먼트 navigate
     *
     * @param direction 다음 프래그먼트로 이동할 Direction
     * */
    protected fun navigate(destinationId: Int, args: Bundle? = null) {
        val controller = findNavController()
        controller.navigate(destinationId, args)
    }

    protected fun finish() {
        val fragmentManager = childFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    /**
     * Network 연결 상태 감지
     * */
    private fun observeNetworkStatus() {
        repeatWhenUiStarted {
            networkManager.isConnected.collect { isConnected ->
                if (!isConnected) {
                    handleNetworkDisconnected()
                }
            }
        }
    }
    protected open fun handleNetworkDisconnected() {
        if (activity is GlobalNavigation) {
            (activity as GlobalNavigation).navigateNetworkError()
        }
    }
}