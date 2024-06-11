package su.afk.commercemvvm.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.databinding.RvCartProductItemBinding
import su.afk.commercemvvm.util.getPriceProduct

class CartProductAdapter: RecyclerView.Adapter<CartProductAdapter.CartProductAdapterViewHolder>() {

    inner class CartProductAdapterViewHolder(val binding: RvCartProductItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(cartProduct: CartProduct){
            binding.apply {
                Glide.with(itemView)
                    .load(cartProduct.product.images[0])
                    .placeholder(R.drawable.ic_replay) // placeholder для улучшения UX
                    .error(R.drawable.ic_error) // изображение ошибки для отображения, если изображение не загрузилось
                    .into(imageCartProduct)
                tvCartProductName.text = cartProduct.product.name
                tvCountProduct.text = cartProduct.quantity.toString()

                //если есть скидка
                val priceAfterOffer = cartProduct.product.offerPercentage.getPriceProduct(cartProduct.product.price)
                tvCartProductPrice.text = "${String.format("%.1f", priceAfterOffer)} ₽"

//                Log.e("TAG", "cartProduct.selectColor: ${cartProduct.selectColor}")
//                Log.e("TAG", "cartProduct.selectSize: ${cartProduct.selectSize}")
                // выбранный цвет
                imageCartColor.setImageDrawable(ColorDrawable(cartProduct.selectColor?: Color.TRANSPARENT))
                // выбранный размер
                tvCartSize.text = cartProduct.selectSize?.trim() ?: "".also {
                    imageCartSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductAdapterViewHolder {
        return CartProductAdapterViewHolder(
            RvCartProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductAdapterViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null
}