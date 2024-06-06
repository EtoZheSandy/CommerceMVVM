package su.afk.commercemvvm.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import su.afk.commercemvvm.R
import su.afk.commercemvvm.databinding.ActivityShoppingBinding

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
//    lateinit var binding: ActivityShoppingBinding

    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavBar.setupWithNavController(navController)
    }
}