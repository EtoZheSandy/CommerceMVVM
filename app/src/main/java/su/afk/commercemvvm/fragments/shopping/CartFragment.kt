package su.afk.commercemvvm.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.CartProductAdapter
import su.afk.commercemvvm.databinding.FragmentCartBinding
import su.afk.commercemvvm.firebase.FirebaseCommon
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.VerticalItemDecoration
import su.afk.commercemvvm.util.bottomBarVisibilityShow
import su.afk.commercemvvm.viewModels.CartViewModel

class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
//    private val viewModel by viewModels<CartViewModel>()
    // используем activityViewModels потому что мы инициализировали уже CartViewModel в shoppingActivity
    // и мы не хотим создавать два экземпляра viewModel
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvCart()

        // отображение товаров в адаптере
        lifecycleScope.launch {
            viewModel.cartProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty()) {
                            showCartEmpty()
                            hideOtherViews()
                        } else {
                            hideCartEmpty()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        // общая цена корзины
        var totalPrice = 0f

        // отображение общей цены всех товаров
        lifecycleScope.launch {
            viewModel.productPrice.collectLatest { price ->
                if(price != null) {
                    totalPrice = price
                    binding.tvTotalPrice.text = "$price ₽"
                }
            }
        }

        // щелчек по товару
        cartAdapter.onProductClick = {
            val bundle = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_detailProductFragment, bundle)
        }

        // увеличение товара в корзине
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(cartProduct = it, quantityChanging = FirebaseCommon.QuantityChanging.INCREASE)
        }

        // уменьшение товара в корзине
        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(cartProduct = it, quantityChanging = FirebaseCommon.QuantityChanging.INCREASE)
        }

        // диалог удаления при клике onMinusClick.product.quantity < 1
        lifecycleScope.launch {
            viewModel.deleteDialog.collectLatest { cartProduct ->
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Удалить товар из корзины")
                    setMessage("Вы уверены что хотите удалить товар их своей корзины?")
                    setNegativeButton("Отменить") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Удалить") {  dialog, _ ->
                        viewModel.deleteCartProduct(cartProduct = cartProduct)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        // для оформления заказа передаем ключи из nav totalPrice и products
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice = totalPrice,
                products = cartAdapter.differ.currentList.toTypedArray(),
                payment = true
            )
            findNavController().navigate(action)
        }


        //кнопка назад
//        binding.imageCloseCart.setOnClickListener {
//            findNavController().navigateUp()
//        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideCartEmpty() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showCartEmpty() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupRvCart() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        bottomBarVisibilityShow()
    }
}