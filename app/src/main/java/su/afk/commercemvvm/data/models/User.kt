package su.afk.commercemvvm.data.models

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val photoUrl: String = "",
) {
    constructor(): this("", "", "", "")
}
