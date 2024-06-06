package su.afk.commercemvvm.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.BottomProductAdapter
import su.afk.commercemvvm.adapters.TopProductAdapter
import su.afk.commercemvvm.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    // protected для доступа только в дочерних фрагментах
//    protected lateinit var bestAdapter: TopProductAdapter
    // lazy инициализируем когда обращаемся первый раз
    protected val bestAdapter: TopProductAdapter by lazy { TopProductAdapter() }
//    protected lateinit var offerAdapter: BottomProductAdapter
    protected val offerAdapter: BottomProductAdapter by lazy { BottomProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBestRv()
        setupOfferRv()

        // горизонтальная прокрутка акционных товаров
        binding.rvBestProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // если не можем прокручивать по горизонтали и x = 0
                if(!recyclerView.canScrollVertically(1) && dx != 0) {
                    pigingBestRequest() // подгружаем еще акционных продуктов
                }
            }
        })

        // отслеживаем состояние прокрутки нижних товаров
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // если достигли низа экрана
            if(v.getChildAt(0).bottom <= v.height + scrollY) {
                pigingOfferRequest()
            }
        })
    }

    fun setupBestRv() {
//        bestAdapter = TopProductAdapter() // инициализирует в lazy
        binding.rvBestProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestAdapter
        }
    }

    open fun pigingBestRequest() {}

    fun setupOfferRv() {
//        offerAdapter = BottomProductAdapter()
        binding.rvOffersProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            adapter = offerAdapter
        }
    }

    open fun pigingOfferRequest() {}
}