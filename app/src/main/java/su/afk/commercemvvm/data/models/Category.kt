package su.afk.commercemvvm.data.models

sealed class Category(
    val category: String
) {
    object Chair: Category("стул")
    object Wardrobe: Category("шкаф")
    object Table: Category("стол")
    object Divan: Category("диван")
    object Bed: Category("кровать")
}