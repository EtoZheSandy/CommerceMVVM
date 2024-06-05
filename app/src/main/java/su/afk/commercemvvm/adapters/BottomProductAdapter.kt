package su.afk.commercemvvm.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.databinding.RvItemProductBinding

class BottomProductAdapter: RecyclerView.Adapter<BottomProductAdapter.BottomProductAdapterViewHolder>() {

    inner class BottomProductAdapterViewHolder(private val binding: RvItemProductBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageRvItem)
                tvItemName.text = product.name

                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price

                    tvItemPriceNew.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    tvItemPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // перечеркивание текста
                }
                tvItemPrice.text = product.price.toString()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomProductAdapterViewHolder {
        return BottomProductAdapterViewHolder(
            RvItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BottomProductAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}