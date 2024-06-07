package su.afk.commercemvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import su.afk.commercemvvm.databinding.ViewPagerImageItemBinding

class ViewPager2ImagesDetail: RecyclerView.Adapter<ViewPager2ImagesDetail.ViewPager2ImagesDetailViewHolder>() {

    inner class ViewPager2ImagesDetailViewHolder(val binding: ViewPagerImageItemBinding):
            ViewHolder(binding.root) {
                fun bind(imageUrl: String) {
                    Glide.with(itemView).load(imageUrl).into(binding.imageDetailProduct)
                }
            }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPager2ImagesDetailViewHolder {
        return ViewPager2ImagesDetailViewHolder(
            ViewPagerImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2ImagesDetailViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}