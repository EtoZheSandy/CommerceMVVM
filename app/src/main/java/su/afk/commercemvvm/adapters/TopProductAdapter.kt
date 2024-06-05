package su.afk.commercemvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.databinding.RvItemSpecialBinding

class TopProductAdapter: RecyclerView.Adapter<TopProductAdapter.TopProductAdapterViewHolder>() {

    inner class TopProductAdapterViewHolder(private val binding: RvItemSpecialBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView).load(product.images[0]).into(imageRvItem)
                    tvItemName.text = product.name
                    tvItemPrice.text = product.price.toString()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductAdapterViewHolder {
        return TopProductAdapterViewHolder(
            RvItemSpecialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TopProductAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }


}