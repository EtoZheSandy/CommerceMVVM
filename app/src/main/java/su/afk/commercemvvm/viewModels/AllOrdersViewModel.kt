package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Order
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel() {


    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Start())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
    }

    fun getAllOrders() {
        viewModelScope.launch { _allOrders.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)

                viewModelScope.launch { _allOrders.emit(Resource.Success(orders)) }
            }
            .addOnFailureListener {
                viewModelScope.launch { _allOrders.emit(Resource.Error(it.message.toString())) }
            }
    }

}