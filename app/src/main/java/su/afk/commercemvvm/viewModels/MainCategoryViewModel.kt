package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _productsTop = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val productTop: StateFlow<Resource<List<Product>>> = _productsTop

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            _productsTop.emit(Resource.Loading())
        }
        // получаем из коллекции products по category фильтр special
        firestore.collection("products")
            .whereEqualTo("category", "special").get()
            .addOnSuccessListener { result ->
                // получаем продукты и кастим их в data class Product
                val productTopList = result.toObjects(Product::class.java)

                viewModelScope.launch {
                    _productsTop.emit(Resource.Success(productTopList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _productsTop.emit(Resource.Error(it.message.toString()))
                }
            }

    }


}