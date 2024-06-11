package su.afk.commercemvvm.fragments.shopping

import android.graphics.Bitmap
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.DetailColorsAdapter
import su.afk.commercemvvm.adapters.DetailImagesViewPager2
import su.afk.commercemvvm.adapters.DetailSizeAdapter
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.databinding.FragmentDetailProductBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.bottomBarVisibilityHide
import su.afk.commercemvvm.viewModels.DetailsViewModel

@AndroidEntryPoint
class DetailProductFragment: Fragment() {
    private val args by navArgs<DetailProductFragmentArgs>()

    private lateinit var binding: FragmentDetailProductBinding
    private val viewPagerAdapter by lazy { DetailImagesViewPager2() }
    private val sizeAdapter by lazy { DetailSizeAdapter() }
    private val colorsAdapter by lazy { DetailColorsAdapter() }

    private var selectedColor: Int? = null
    private var selectedSize: String? = null

    private val viewModel by viewModels<DetailsViewModel>()

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
            tvProductPrice.text = getNewPrice(priceProduct = product, discountPercentage = product.offerPercentage)
            tvProductDescription.text = product.description

            if(product.colors.isNullOrEmpty()) tvProductColor.isVisible = false
            if(product.sizes.isNullOrEmpty()) tvProductSize.isVisible = false
        }

        // устанавливаем адаптеры для картинок размером и цветов если они не пусты
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizeAdapter.differ.submitList(it) }

        // слушатель нажатий по цвету/размеру
        sizeAdapter.onItemClick = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        // при клике добавляет товар в корзину
        binding.buttonAddCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(
                product = product, quantity = 1, selectColor = selectedColor, selectSize = selectedSize
            ))
        }

        // кнопка добавить в корзину
        lifecycleScope.launch {
            viewModel.addToCart.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonAddCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddCart.revertAnimation()
//                        binding.buttonAddCart.setBackgroundColor(resources.getColor(R.color.my_dark_blue))
//                        Toast.makeText(requireContext(), "Добавлен в корзину", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.buttonAddCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupImagePagerRv() {
        binding.apply {
            viewPagerImageProduct.adapter = viewPagerAdapter // адаптер для вывода картинки товара
        }
    }
    private fun setupColorRv() {
        binding.rvProductColors.apply {
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

    private fun getNewPrice(priceProduct: Product, discountPercentage: Float?): String {
        var priceAfterOffer = priceProduct.price // цена продукта

        // если есть скидка
        discountPercentage?.let {
            val remainingPricePercentage = 1f - discountPercentage
            priceAfterOffer = remainingPricePercentage * priceProduct.price
        }

        // Новая цена
        return "${String.format("%.0f", priceAfterOffer)} ₽"
    }
}