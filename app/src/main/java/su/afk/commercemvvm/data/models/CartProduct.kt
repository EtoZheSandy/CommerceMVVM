package su.afk.commercemvvm.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectColor: Int? = null,
    val selectSize: String? = null,
): Parcelable { // конструктор нужен для toObject из FireBase
    constructor(): this(Product(), 1, null, null)
}