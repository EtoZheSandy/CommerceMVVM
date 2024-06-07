package su.afk.commercemvvm.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import su.afk.commercemvvm.databinding.RvItemDtColorBinding

class DetailColorsAdapter: RecyclerView.Adapter<DetailColorsAdapter.ColorsAdapterVH>() {

    private var selectedPosition = -1

    inner class ColorsAdapterVH(private val binding: RvItemDtColorBinding): ViewHolder(binding.root) {
        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color) // получаем цвет
            binding.imageColor.setImageDrawable(imageDrawable) // применяем его
            // если выбран цвет
            if (position == selectedPosition) {
                binding.apply {
                    imageCircle.visibility = View.VISIBLE
                    imagePicker.visibility = View.VISIBLE
                }
            } else { // цвет не выбран
                binding.apply {
                    imageCircle.visibility = View.INVISIBLE
                    imagePicker.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsAdapterVH {
        return ColorsAdapterVH(
            RvItemDtColorBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsAdapterVH, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0) notifyItemChanged(selectedPosition) // отменяем старый выбранный элемент
            selectedPosition = holder.adapterPosition // выбираем новый элемент
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(color)
        }
    }

    var onItemClick:((Int) -> Unit)? = null
}