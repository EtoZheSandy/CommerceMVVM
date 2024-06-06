package su.afk.commercemvvm.viewModels.myFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import su.afk.commercemvvm.data.models.Category
import su.afk.commercemvvm.viewModels.CategoryViewModel

class BaseCategoryViewFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore = firestore, category = category) as T
    }
}