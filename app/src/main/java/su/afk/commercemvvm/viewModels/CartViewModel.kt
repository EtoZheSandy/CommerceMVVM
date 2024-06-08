package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.firebase.FirebaseCommon
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.getPriceProduct
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


    // общий прайс корзины
    val productPrice = cartProducts.map {
        when(it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }
    // прайс всех товаров в корзине
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getPriceProduct(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }
    private var cartProductDocument = emptyList<DocumentSnapshot>()

    init {
        gerCartProducts()
    }

    // получаем товары из корзины юзера
    private fun gerCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }

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
    fun changeQuantity(cartProduct: CartProduct, quantityChanging: FirebaseCommon.QuantityChanging) {
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
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {

                    if(cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }

                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
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


    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    // удаление товара из корзины
    fun deleteCartProduct(cartProduct: CartProduct) {
        // ищем индекс продукта в корзине
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if(index != null && index != -1) {
            val documentId = cartProductDocument[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete() // удаляем товар(документ) из корзины
        }
    }
}