package su.afk.commercemvvm.data.models

sealed class OrderStatus(val status: String) {

    object Ordered: OrderStatus("Создано")
    object Cancel: OrderStatus("Отменен")
    object Confirmed: OrderStatus("Подтвержден")
    object Shipped: OrderStatus("Отправлен")
    object Delivered: OrderStatus("Доставлен")
    object Returned: OrderStatus("Возвращен")
}