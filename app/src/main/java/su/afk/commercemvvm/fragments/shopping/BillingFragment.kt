package su.afk.commercemvvm.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.AddressAdapter
import su.afk.commercemvvm.adapters.BillingProductAdapter
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.data.models.Order
import su.afk.commercemvvm.data.models.OrderStatus
import su.afk.commercemvvm.databinding.FragmentBillingBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.BillingViewModel
import su.afk.commercemvvm.viewModels.OrderViewModel

@AndroidEntryPoint
class BillingFragment: Fragment(R.layout.fragment_billing) {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()

    // получаем переданные параметры от nav
    private val args by navArgs<BillingFragmentArgs>()
    private var product = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        product = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvAddress()
        setupRvBillingProduct()


        lifecycleScope.launch {
            billingViewModel.address.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // для добавления адреса
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        //отображенние переанных элементов корзины
        billingProductAdapter.differ.submitList(product)

        // цена корзины
        binding.tvTotalPrice.text = totalPrice.toString()


        // получаем адресс доставки из адаптера
        addressAdapter.onClick = {
            selectedAddress = it
        }

        // оформление заказа
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Выберите адрес доставки", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                showOrderDialog()
            }
        }


        // после подтверждения заказа делаем переход
        lifecycleScope.launch {
            orderViewModel.order.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Заказ оформлен", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRvAddress() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
        }
    }

    private fun setupRvBillingProduct() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductAdapter
        }
    }

    private fun showOrderDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Оформить заказ")
            setMessage("Подтвердить оформление заказа")
            setNegativeButton("Отменить") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Удалить") {  dialog, _ ->
                val order = Order(
                    orderStatus = OrderStatus.Ordered.status,
                    totalPrice = totalPrice,
                    products = product,
                    address = selectedAddress!!
                )
                orderViewModel.placeOrder(order = order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }
}