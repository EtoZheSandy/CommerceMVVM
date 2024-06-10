package su.afk.commercemvvm.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.databinding.RvItemBillingProductsBinding
import su.afk.commercemvvm.util.getPriceProduct

class BillingProductAdapter: Adapter<BillingProductAdapter.BillingVH>() {

    inner class BillingVH(val binding: RvItemBillingProductsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                //если есть скидка
                val priceAfterOffer = billingProduct.product.offerPercentage.getPriceProduct(billingProduct.product.price)
                tvProductCartPrice.text = "${String.format("%.1f", priceAfterOffer)} ₽"

                imageCartProduct.setImageDrawable(ColorDrawable(billingProduct.selectColor?: Color.TRANSPARENT))
                tvCartProductSize.text = billingProduct.selectSize ?:"".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }

    }


    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingVH {
        return BillingVH(
            RvItemBillingProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingVH, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }


    var onClick: ((Address) -> Unit)? = null
}