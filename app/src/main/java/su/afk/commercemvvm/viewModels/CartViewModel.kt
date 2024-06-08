package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.firebase.FirebaseCommon
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {


    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Start())
    val cartProducts = _cartProducts.asStateFlow()


    private var cartProductDocument = emptyList<DocumentSnapshot>()

    init {
        gerCartProducts()
    }

    // получаем товары из корзины юзера
    private fun gerCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection("user")
            .document(auth.uid!!).collection("cart")
            // addSnapshotListener обратный вызов когда cart в этой коллекции меняются он вызывается сам
            // используем его потому что хотим обновлять наш юзер UI
            // при добавление товаров от юзера
            .addSnapshotListener { value, error ->
                if(error != null || value == null) { // если есть ошибка
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocument = value.documents
                    val cartProduct = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProduct)) }
                }
            }
    }

    // изменяем колличество товаров в корзине
    private fun changeQuantity(cartProduct: CartProduct, quantityChanging: FirebaseCommon.QuantityChanging) {

        // ищем индекс продукта в корзине
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
        *  индекс может быть равен -1, если функция [gerCartProducts] delay,
         *  и это приведет к задержке результата, который, как мы ожидаем, будет внутри [_cartProducts]
        * и чтобы предотвратить сбой приложения, мы проверяем index != -1 и index != null
         * */
        if(index != null && index != -1) {
            val documentId = cartProductDocument[index].id
            when(quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> increaseQuantity(documentId)
                FirebaseCommon.QuantityChanging.DECREASE -> decreaseQuantity(documentId)
            }
        } else {

        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId = documentId) { result, exception ->
            if(exception != null) {
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId = documentId) {  result, exception ->
            if(exception != null) {
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }
}