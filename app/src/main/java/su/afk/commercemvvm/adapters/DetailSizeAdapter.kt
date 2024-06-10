package su.afk.commercemvvm.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import su.afk.commercemvvm.databinding.RvItemDtColorBinding
import su.afk.commercemvvm.databinding.RvItemDtSizeBinding

class DetailSizeAdapter: RecyclerView.Adapter<DetailSizeAdapter.SizeAdapterVH>() {

    private var selectedPosition = -1

    inner class SizeAdapterVH(private val binding: RvItemDtSizeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.tvSizePicker.text = size // выбранный размер
            // если выбран размер
            if (position == selectedPosition) {
                binding.apply {
                    imageCircle.visibility = View.VISIBLE
                }
            } else { // размер не выбран
                binding.apply {
                    imageCircle.visibility = View.INVISIBLE
                }
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAdapterVH {
        return SizeAdapterVH(
            RvItemDtSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizeAdapterVH, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0) notifyItemChanged(selectedPosition) // отменяем старый выбранный элемент
            selectedPosition = holder.adapterPosition // выбираем новый элемент
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick:((String) -> Unit)? = null
}