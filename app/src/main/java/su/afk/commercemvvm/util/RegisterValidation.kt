package su.afk.commercemvvm.util

sealed class RegisterValidation() {
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}

data class RegisterFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
)