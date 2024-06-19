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
class OrderViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Start())
    val order = _order.asStateFlow()


    // создание заказа
    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }

        firestore.runBatch { batch ->
            // 1. добавляем заказ в коллекцию заказов юзера
            // 2. добавляем заказ в коллекцию общих заказов
            // 3. удаляем товары из корзины юзера

            firestore.collection("user").document(auth.uid!!)
                .collection("orders").document()
                .set(order)

            firestore.collection("orders").document().set(order)

            firestore.collection("user").document(auth.uid!!)
                .collection("cart")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }


    }
}