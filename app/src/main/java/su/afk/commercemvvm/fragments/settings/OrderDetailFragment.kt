package su.afk.commercemvvm.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firestore.v1.StructuredQuery.Order
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.BillingProductAdapter
import su.afk.commercemvvm.data.models.OrderStatus
import su.afk.commercemvvm.data.models.getOrderStatus
import su.afk.commercemvvm.databinding.FragmentDetailProductBinding
import su.afk.commercemvvm.databinding.FragmentOrderDetailBinding

class OrderDetailFragment: Fragment(R.layout.fragment_order_detail) {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setupRv()

        binding.apply {

            tvOrderId.text = "Заказ #${order.orderId}"
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = order.totalPrice.toString()

            // задаем возможные статусы заказа
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            // узнаем текущий статус что бы передать его индекс
            val orderCurrentStatus = when(getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            // задаем индекс
            stepView.go(orderCurrentStatus, false)
            if(orderCurrentStatus == 3) {
                stepView.done(true) // если статус 3 то это полностью выполненный заказ
            }
        }

        // передаем продукты из заказа для отображения
        billingProductAdapter.differ.submitList(order.products)
    }

    private fun setupRv() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

}