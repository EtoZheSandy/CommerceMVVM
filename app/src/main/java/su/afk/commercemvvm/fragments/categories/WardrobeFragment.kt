package su.afk.commercemvvm.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.Category
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.CategoryViewModel
import su.afk.commercemvvm.viewModels.myFactory.BaseCategoryViewFactory
import javax.inject.Inject

@AndroidEntryPoint
class WardrobeFragment: BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewFactory(firestore = firestore, category = Category.Wardrobe)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // для акционных продуктов
        lifecycleScope.launch {
            viewModel.bestProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showBestLoading()
                    }
                    is Resource.Success -> {
                        Log.d("TAG", "bestAdapter.Success: ${it.data}")
                        bestAdapter.differ.submitList(it.data)
                        hideBestLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideBestLoading()
                    }
                    else -> Unit
                }
            }
        }

        // для безакционных продуктов
        lifecycleScope.launch {
            viewModel.offerProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        Log.d("TAG", "offerAdapter.Success: ${it.data}")
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    // для пагинации дописать
    override fun pigingBestRequest() {
        viewModel.bestProducts
    }
    // для пагинации дописать
    override fun pagingOfferRequest() {
        viewModel.offerProducts
    }
}