package su.afk.commercemvvm.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.TopProductAdapter
import su.afk.commercemvvm.databinding.FragmentMainCategoryBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.MainCategoryViewModel

@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var adapterTop: TopProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        lifecycleScope.launch {
            viewModel.productTop.collect{
                when(it){
                    is Resource.Loading -> { binding.progressBar.isVisible = true }
                    is Resource.Success -> {
                        adapterTop.differ.submitList(it.data)
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Error -> {
                        Log.e("MainCategoryFragment", it.message.toString())
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                        binding.progressBar.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRv() {
        adapterTop = TopProductAdapter()
        binding.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterTop
        }
    }
}