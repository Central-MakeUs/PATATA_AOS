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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseFragment<T: ViewDataBinding>(@LayoutRes val layoutRes: Int)
    : Fragment() {
    private var _binding: T? = null
    val binding get() = _binding!!

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
    protected fun navigate(destinationId: Int) {
        val controller = findNavController()
        controller.navigate(destinationId)
    }

    protected fun finish() = requireActivity().onBackPressedDispatcher.onBackPressed()
}