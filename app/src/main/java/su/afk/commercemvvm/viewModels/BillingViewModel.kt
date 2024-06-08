package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {


    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Start())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }

    fun getUserAddress() {
        viewModelScope.launch { _address.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("address")
            // поскольку юзер может добавить адресс и после вернутся на предыдущий фрагмент мы отслеживаем
            // добавление новых адресов и сразу же их ловим тут
            .addSnapshotListener { value, error ->
                if(error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                } else {

                    val address = value?.toObjects(Address::class.java)
                    viewModelScope.launch { _address.emit(Resource.Success(address!!)) }
                }
            }
    }


}