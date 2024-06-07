package su.afk.commercemvvm.data.models

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectColor: Int? = null,
    val selectSize: String? = null,
) { // конструктор нужен для toObject из FireBase
    constructor(): this(Product(), 1, null, null)
}