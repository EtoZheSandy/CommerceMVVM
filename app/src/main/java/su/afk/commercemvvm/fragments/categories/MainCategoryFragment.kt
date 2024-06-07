package su.afk.commercemvvm.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.NestedScrollingChild
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.BottomProductAdapter
import su.afk.commercemvvm.adapters.MediumProductAdapter
import su.afk.commercemvvm.adapters.TopProductAdapter
import su.afk.commercemvvm.databinding.FragmentMainCategoryBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.bottomBarVisibilityShow
import su.afk.commercemvvm.viewModels.MainCategoryViewModel

@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var adapterTop: TopProductAdapter
    private lateinit var adapterMedium: MediumProductAdapter
    private lateinit var adapterBottom: BottomProductAdapter
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

        setupRvTop()
        setupRvMedium()
        setupRvBottom()

        adapterTop.onClick = { product ->
            // передаем в bundle arg по ключу равному в nav graf
            val bundle = Bundle().apply { putParcelable("product", product) }
            // переходим к detailProductFragment
            findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }

        adapterMedium.onClick = { product ->
            // передаем в bundle arg по ключу равному в nav graf
            val bundle = Bundle().apply { putParcelable("product", product) }
            // переходим к detailProductFragment
            findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }

        adapterBottom.onClick = { product ->
            // передаем в bundle arg по ключу равному в nav graf
            val bundle = Bundle().apply { putParcelable("product", product) }
            // переходим к detailProductFragment
            findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }

        // отслеживаем состояние прокрутки
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // если достигли низа экрана
            if(v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.getProductsBottom() // подгружаем еще данные
            }
        })

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

        lifecycleScope.launch {
            viewModel.productMedium.collect{
                when(it){
                    is Resource.Loading -> { binding.progressBar.isVisible = true }
                    is Resource.Success -> {
                        adapterMedium.differ.submitList(it.data)
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

        lifecycleScope.launch {
            viewModel.productBottom.collect{
                when(it){
                    is Resource.Loading -> { binding.progressBarBottom.isVisible = true }
                    is Resource.Success -> {
                        adapterBottom.differ.submitList(it.data)
                        binding.progressBarBottom.isVisible = false
                    }
                    is Resource.Error -> {
                        Log.e("MainCategoryFragment", it.message.toString())
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                        binding.progressBarBottom.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRvTop() {
        adapterTop = TopProductAdapter()
        binding.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterTop
        }
    }

    private fun setupRvMedium(){
        adapterMedium = MediumProductAdapter()
        binding.rvMediumProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterMedium
        }
    }

    private fun setupRvBottom(){
        adapterBottom = BottomProductAdapter()
        binding.rvBottomProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            adapter = adapterBottom
        }
    }

    override fun onResume() {
        super.onResume()
        bottomBarVisibilityShow() // возвращаем видимость бара
    }
}