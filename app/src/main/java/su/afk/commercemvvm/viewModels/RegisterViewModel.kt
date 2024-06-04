package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import su.afk.commercemvvm.data.models.User
import su.afk.commercemvvm.util.RegisterFailedState
import su.afk.commercemvvm.util.RegisterValidation
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.validateEmail
import su.afk.commercemvvm.util.validatePassword
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Start())
    val register: Flow<Resource<FirebaseUser>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading()) // после клика ставим статус на загрузку
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        _register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFailedState = RegisterFailedState(
                email = validateEmail(user.email),
                password = validatePassword(password)
            )
           runBlocking {
               _validation.send(registerFailedState) // отправляем ошибку
           }
        }
    }

    // Разница с flow в том что channel не принимает начальное значение
    private val _validation = Channel<RegisterFailedState>()
    val validation = _validation.receiveAsFlow() // кастим в flow


    private fun checkValidation(user: User, password: String): Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }

}