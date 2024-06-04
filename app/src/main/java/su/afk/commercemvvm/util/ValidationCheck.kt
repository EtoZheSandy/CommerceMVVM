package su.afk.commercemvvm.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if(email.isEmpty()) {
        return RegisterValidation.Failed("Email не может быть пустым")
    }
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return RegisterValidation.Failed("Неверный формат email")
    }
    
    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if(password.isEmpty()) {
        return RegisterValidation.Failed("Пароль не может быть пустым")
    }

    if(password.length < 6) {
        return RegisterValidation.Failed("Пароль короче 6 символов")
    }

    return RegisterValidation.Success
}