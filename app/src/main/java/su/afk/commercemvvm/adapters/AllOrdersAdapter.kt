package su.afk.commercemvvm.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.Order
import su.afk.commercemvvm.data.models.OrderStatus
import su.afk.commercemvvm.data.models.getOrderStatus
import su.afk.commercemvvm.databinding.RvOrderItemBinding

class AllOrdersAdapter: RecyclerView.Adapter<AllOrdersAdapter.OrdersVH>() {

    inner class OrdersVH(val binding: RvOrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resource = itemView.resources

                // для определения цвета статуса
                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> ColorDrawable(resource.getColor(R.color.my_orange_yellow))
                    is OrderStatus.Cancel -> ColorDrawable(resource.getColor(R.color.my_red))
                    is OrderStatus.Shipped -> ColorDrawable(resource.getColor(R.color.my_green))
                    is OrderStatus.Confirmed -> ColorDrawable(resource.getColor(R.color.my_green))
                    is OrderStatus.Delivered -> ColorDrawable(resource.getColor(R.color.my_green))
                    is OrderStatus.Returned -> ColorDrawable(resource.getColor(R.color.my_red))
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersVH {
        return OrdersVH(
            RvOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersVH, position: Int) {
        val order = differ.currentList[position]

        holder.bind(order)

    }

    // анонимная функция передаем Order и ничего не ожидаем
    var onClick: ((Order) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}