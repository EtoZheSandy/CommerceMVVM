package su.afk.commercemvvm.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.util.Constanse.INTRODUCTION_KEY
import javax.inject.Inject

@HiltViewModel
class IntoDuctionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser


        Log.d("TAG", "Вошел или юзер?: ${user != null}")
        Log.d("TAG", "Юзер: $user")
        Log.d("TAG", "isButtonClicked: $isButtonClicked")
        if(user != null) { // если юзер вошел в систему
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) { // если юзер уже кликал на приветсвенную кнопку
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        } else {
            Unit
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }

    companion object {
        const val SHOPPING_ACTIVITY = 21
        const val ACCOUNT_OPTION_FRAGMENT = 20
    }
}