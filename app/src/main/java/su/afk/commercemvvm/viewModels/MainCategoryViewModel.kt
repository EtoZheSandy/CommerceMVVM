package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Product
import su.afk.commercemvvm.fragments.shopping.HomeFragment
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _productsTop = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val productTop: StateFlow<Resource<List<Product>>> = _productsTop

    private val _productsMedium = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val productMedium: StateFlow<Resource<List<Product>>> = _productsMedium

    private val _productsBottom = MutableStateFlow<Resource<List<Product>>>(Resource.Start())
    val productBottom: StateFlow<Resource<List<Product>>> = _productsBottom

    private val pagingInfo = PagingInfo()

    init {
        getProductsTop()
        getProductsMedium()
        getProductsBottom()
    }

    private fun getProductsTop() {
        viewModelScope.launch {
            _productsTop.emit(Resource.Loading())
        }
        // получаем из коллекции products по category фильтр special
        firestore.collection("products")
            .whereEqualTo("category", HomeFragment.CATEGORY_TOP_FILTER).get()
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

    private fun getProductsMedium() {
        viewModelScope.launch {
            _productsMedium.emit(Resource.Loading())
        }
        // получаем из коллекции products по category фильтр special
        firestore.collection("products")
            .whereEqualTo("category", HomeFragment.CATEGORY_MEDIUM_FILTER).get()
            .addOnSuccessListener { result ->
                // получаем продукты и кастим их в data class Product
                val productMediumList = result.toObjects(Product::class.java)

                viewModelScope.launch {
                    _productsMedium.emit(Resource.Success(productMediumList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _productsMedium.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun getProductsBottom() {
        // если новый список == старому списка значит новых элементов нету
        if(!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _productsBottom.emit(Resource.Loading())
            }
            // получаем 10 продуктов из коллекции products
            firestore.collection("products").limit(pagingInfo.pageBottom * 10).get()
                .addOnSuccessListener { result ->
                    // получаем продукты и кастим их в data class Product
                    val productBottomList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = productBottomList == pagingInfo.oldBottomProducts // сравниваем сохранный список и новый
                    pagingInfo.oldBottomProducts = productBottomList // сохраняем новый список
                    viewModelScope.launch {
                        _productsBottom.emit(Resource.Success(productBottomList))
                    }
                    pagingInfo.pageBottom++ // увеличиваю страницу на 1
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _productsBottom.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    // для итерации по страницам дабы не грузить все элементы
    internal data class PagingInfo(
        var pageBottom: Long = 1,
        var oldBottomProducts: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )
}