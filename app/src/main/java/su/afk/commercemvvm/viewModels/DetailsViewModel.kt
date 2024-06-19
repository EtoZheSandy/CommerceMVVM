package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.CartProduct
import su.afk.commercemvvm.firebase.FirebaseCommon
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Start())
    val addToCart = _addToCart.asStateFlow()


    /** 2 сценария
    * 1 товар уже находится в корзине и юзер снова нажимает на добавить в корзину то мы просто увеличиваем количество
    * 2 если товар был другим или вообще не был добавлен в корзину то мы добавляем его */
    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        /** ищем в collection user документ по user.uid
        // если коллекция cart не создана она будет создана
        // проверяем есть ли товар в корзине */
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener { querySnapshot ->
                // проверяем пуст ли документ, если пуст то товар еще не добавлен в корзину
                val documents = querySnapshot.documents

                if (documents.isEmpty()) {
                    addNewProduct(cartProduct = cartProduct)
                } else {
                    val product = documents.first().toObject(CartProduct::class.java)
                    if(product == cartProduct) {
                        val documentId = documents.first().id
                        increaseQuantity(id = documentId, cartProduct = cartProduct)
                    } else {
                        addNewProduct(cartProduct = cartProduct)
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }


    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct = cartProduct) { addProduct, error ->
            if (error == null) {
                viewModelScope.launch { _addToCart.emit(Resource.Success(addProduct!!)) }
            } else {
                viewModelScope.launch { _addToCart.emit(Resource.Error(error.message.toString())) }
            }
        }
    }

    private fun increaseQuantity(id: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId = id) { _, exception ->
            if (exception == null) {
                viewModelScope.launch { _addToCart.emit(Resource.Success(cartProduct)) }
            } else {
                viewModelScope.launch { _addToCart.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }
}