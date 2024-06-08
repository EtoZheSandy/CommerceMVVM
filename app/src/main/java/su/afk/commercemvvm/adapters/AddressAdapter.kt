package su.afk.commercemvvm.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.databinding.RvItemAddressBinding

class AddressAdapter : Adapter<AddressAdapter.AddressVH>() {

    inner class AddressVH(val binding: RvItemAddressBinding): ViewHolder(binding.root) {
        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                buttonAddress.text = address.addressTitle
                if(isSelected){
                    buttonAddress.background = ColorDrawable(itemView.resources.getColor(R.color.my_blue))
                } else {
                    buttonAddress.background = ColorDrawable(itemView.resources.getColor(R.color.white))
                }
            }
        }

    }


    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        return AddressVH(
            RvItemAddressBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedAddress = -1 // выбранный адресс доставки
    // если адресс выбран то кнопку красив в синий показывая что она выбрана

    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        val address = differ.currentList[position]

        holder.bind(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if(selectedAddress >= 0) {
                notifyItemChanged(selectedAddress) // отменяем привязку что бы выдать ее заново
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress) // выбираем новый элемент
            onClick?.invoke(address)
        }
    }


    var onClick: ((Address) -> Unit)? = null
}