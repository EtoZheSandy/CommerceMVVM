package su.afk.commercemvvm.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import su.afk.commercemvvm.adapters.DetailColorsAdapter
import su.afk.commercemvvm.adapters.DetailImagesViewPager2
import su.afk.commercemvvm.adapters.DetailSizeAdapter
import su.afk.commercemvvm.databinding.FragmentDetailProductBinding
import su.afk.commercemvvm.util.bottomBarVisibilityHide

class DetailProductFragment: Fragment() {
    private val args by navArgs<DetailProductFragmentArgs>()

    private lateinit var binding: FragmentDetailProductBinding
    private val viewPagerAdapter by lazy { DetailImagesViewPager2() }
    private val sizeAdapter by lazy { DetailSizeAdapter() }
    private val colorsAdapter by lazy { DetailColorsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomBarVisibilityHide() // скрываем нижний бар
        binding = FragmentDetailProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product // получаем product из переданых аргументов nav graf

        setupImagePagerRv()
        setupColorRv()
        setupSizeRv()

        // клик по крестику
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp() // перемещаемся вверх по навигации (обратно)
        }


        binding.apply { // устанавливаем описание товара
            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} ₽"
            tvProductDescription.text = product.description

            if(product.colors.isNullOrEmpty()) tvProductColor.isVisible = false
            if(product.sizes.isNullOrEmpty()) tvProductSize.isVisible = false
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizeAdapter.differ.submitList(it) }
    }

    private fun setupImagePagerRv() {
        binding.apply {
            viewPagerImageProduct.adapter = viewPagerAdapter // адаптер для вывода картинки товара
        }
    }
    private fun setupColorRv() {
        binding.rvProductSize.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
    private fun setupSizeRv() {
        binding.rvProductSize.apply {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}