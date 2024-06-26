package su.afk.commercemvvm.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.HomeViewPagerAdapter
import su.afk.commercemvvm.databinding.FragmentHomeBinding
import su.afk.commercemvvm.fragments.categories.WardrobeFragment
import su.afk.commercemvvm.fragments.categories.ChairFragment
import su.afk.commercemvvm.fragments.categories.BedFragment
import su.afk.commercemvvm.fragments.categories.DivanFragment
import su.afk.commercemvvm.fragments.categories.MainCategoryFragment
import su.afk.commercemvvm.fragments.categories.TableFragment

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            WardrobeFragment(),
            TableFragment(),
            ChairFragment(),
            DivanFragment(),
            BedFragment()
        )

        // запрет свайпать в окне просмотра
        binding.viewPager.isUserInputEnabled = false


        val adapterViewPager = HomeViewPagerAdapter(categoryFragments, childFragmentManager, lifecycle)

        // Вывод названий фрагментов в tab bar сверху для переключения между ними
        binding.viewPager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Главная"
                1 -> tab.text = CATEGORY_1
                2 -> tab.text = CATEGORY_2
                3 -> tab.text = CATEGORY_3
                4 -> tab.text = CATEGORY_4
                5 -> tab.text = CATEGORY_5
            }
        }.attach()


    }

    companion object{
        const val CATEGORY_1 = "Шкаф"
        const val CATEGORY_2 = "Стол"
        const val CATEGORY_3 = "Стул"
        const val CATEGORY_4 = "Диван"
        const val CATEGORY_5 = "Кровать"

        const val CATEGORY_TOP_FILTER = "выгода"
        const val CATEGORY_MEDIUM_FILTER = "акция"
    }
}