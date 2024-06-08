package su.afk.commercemvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.databinding.RvItemMediumBinding
import su.afk.commercemvvm.util.getPriceProduct


class MediumProductAdapter: RecyclerView.Adapter<MediumProductAdapter.MediumProductAdapterViewHolder>() {

    inner class MediumProductAdapterViewHolder(private val binding: RvItemMediumBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageRvItem)
                tvItemName.text = product.name

//                product.offerPercentage?.let {
//                    val remainingPricePercentage = 1f - it
//                    val priceAfterOffer = remainingPricePercentage * product.price
                    val priceAfterOffer = product.offerPercentage.getPriceProduct(product.price)
                    tvItemPrice.text = "${String.format("%.1f", priceAfterOffer)} ₽"
//                }
                if(product.offerPercentage == null) {
                    tvItemPrice.isVisible = false
                }
                tvItemPriceOld.text = product.price.toString()
                tvItemName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediumProductAdapterViewHolder {
        return MediumProductAdapterViewHolder(
            RvItemMediumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MediumProductAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null
}