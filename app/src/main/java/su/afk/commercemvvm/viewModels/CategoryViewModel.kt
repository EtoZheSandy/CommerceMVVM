package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Category
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.util.Resource

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val bestProducts = _bestProducts.asStateFlow()

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val offerProducts = _offerProducts.asStateFlow()

    init {
        getBestProducts()
        getOfferProducts()
    }

    // акционные продукты из категории
    fun getBestProducts() {
        firestore.collection("products")
            .whereEqualTo("category", category.category) // ищем по category переданную category
            .whereNotEqualTo("offerPercentage", null)
            .limit(10).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    // все продукты из категории
    fun getOfferProducts() {
        firestore.collection("products")
            .whereEqualTo("category", category.category) // ищем по category переданную category
            .whereEqualTo("offerPercentage", null) // все продукты без акции
            .limit(10).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}