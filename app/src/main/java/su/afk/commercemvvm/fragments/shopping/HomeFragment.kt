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
import su.afk.commercemvvm.fragments.categories.AccessoryFragment
import su.afk.commercemvvm.fragments.categories.ChairFragment
import su.afk.commercemvvm.fragments.categories.CupboardFragment
import su.afk.commercemvvm.fragments.categories.FurnitureFragment
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
            CupboardFragment(),
            FurnitureFragment(),
            TableFragment(),
            AccessoryFragment(),
            ChairFragment()
        )

        val adapterViewPager = HomeViewPagerAdapter(categoryFragments, childFragmentManager, lifecycle)

        // Вывод названий фрагментов в tab bar сверху для переключения между ними
        binding.viewPager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = CATEGORY_1
                2 -> tab.text = CATEGORY_2
                3 -> tab.text = CATEGORY_3
                4 -> tab.text = CATEGORY_4
                5 -> tab.text = CATEGORY_5
            }
        }.attach()


    }

    companion object{
        const val CATEGORY_1 = "Cupboard"
        const val CATEGORY_2 = "Furniture"
        const val CATEGORY_3 = "Table"
        const val CATEGORY_4 = "Accessory"
        const val CATEGORY_5 = "Chair"

        const val CATEGORY_FB = "special"
    }
}