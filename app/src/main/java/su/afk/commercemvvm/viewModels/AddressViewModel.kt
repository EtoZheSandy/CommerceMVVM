package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Start())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _errorAddNewAddress = MutableSharedFlow<String>()
    val errorAddNewAddress = _errorAddNewAddress.asSharedFlow()


    fun addAddress(address: Address) {
        viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
        val validateInput = validateInputAddress(address)
        if(validateInput) {
            firestore.collection("address").document(auth.uid!!).collection("address")
                .document().set(address) // сохраняем адрес доставки
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else { // если есть пустое поле
            viewModelScope.launch {
                _errorAddNewAddress.emit("Все поля обязательны для заполнения")
            }
        }
    }

    // проверка вводимых значений
    private fun validateInputAddress(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty()
    }


}