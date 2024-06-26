package su.afk.commercemvvm.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.BottomProductAdapter
import su.afk.commercemvvm.databinding.FragmentBaseCategoryBinding
import su.afk.commercemvvm.util.bottomBarVisibilityShow

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding

    protected val bestAdapter: BottomProductAdapter by lazy { BottomProductAdapter() }

    protected val offerAdapter: BottomProductAdapter by lazy { BottomProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBestRv()
        setupOfferRv()

        // горизонтальная прокрутка акционных товаров
        binding.rdBestProduct.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // если не можем прокручивать по горизонтали и x = 0
                if(!recyclerView.canScrollVertically(1) && dx != 0) {
                    pagingBestRequest() // подгружаем еще акционных продуктов
                }
            }
        })

        // отслеживаем состояние прокрутки нижних товаров
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // если достигли низа экрана
            if(v.getChildAt(0).bottom <= v.height + scrollY) {
                pagingOfferRequest()
            }
        })


        bestAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }

        offerAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }
    }

    fun setupBestRv() {
        binding.rdBestProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestAdapter
        }
    }

    fun showBestLoading() {
//        binding.progressBestProducts.visibility = View.VISIBLE
    }

    fun hideBestLoading() {
//        binding.progressBestProducts.visibility = View.GONE
    }

    open fun pagingBestRequest() {}

    fun setupOfferRv() {
        binding.rvOffersProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            adapter = offerAdapter
        }
    }

    fun showOfferLoading() {
        binding.progressOffersProducts.visibility = View.VISIBLE
    }

    fun hideOfferLoading() {
        binding.progressOffersProducts.visibility = View.GONE
    }

    open fun pagingOfferRequest() {}


    override fun onResume() {
        super.onResume()
        bottomBarVisibilityShow() // возвращаем видимость бара
    }
}