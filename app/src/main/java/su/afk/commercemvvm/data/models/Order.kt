package su.afk.commercemvvm.data.models

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)
