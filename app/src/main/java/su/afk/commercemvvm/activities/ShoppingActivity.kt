package su.afk.commercemvvm.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.databinding.ActivityShoppingBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.CartViewModel

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavBar.setupWithNavController(navController)

        // товары в корзине
        lifecycleScope.launch {
            viewModel.cartProducts.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0

                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavBar)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.my_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}