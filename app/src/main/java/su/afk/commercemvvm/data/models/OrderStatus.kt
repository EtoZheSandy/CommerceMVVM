package su.afk.commercemvvm.data.models

sealed class OrderStatus(val status: String) {

    object Ordered: OrderStatus("Создано")
    object Cancel: OrderStatus("Отменен")
    object Confirmed: OrderStatus("Подтвержден")
    object Shipped: OrderStatus("Отправлен")
    object Delivered: OrderStatus("Доставлен")
    object Returned: OrderStatus("Возвращен")
}



fun getOrderStatus(status: String): OrderStatus {
    return when(status) {
        "Создано" -> OrderStatus.Ordered
        "Отменен" -> OrderStatus.Cancel
        "Подтвержден" -> OrderStatus.Confirmed
        "Отправлен" -> OrderStatus.Shipped
        "Доставлен" -> OrderStatus.Delivered
        else -> OrderStatus.Returned
    }
}