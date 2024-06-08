package su.afk.commercemvvm.util

fun Float?.getPriceProduct(price: Float): Float {
    // this -> процент
    if(this == null)
        return price

    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}