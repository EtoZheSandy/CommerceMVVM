package su.afk.commercemvvm.util

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import su.afk.commercemvvm.R
import su.afk.commercemvvm.activities.ShoppingActivity

fun Fragment.bottomBarVisibilityHide() {
    val bottomNavigationBarView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavBar)
    // если навигация видна то теперь будет не видна и наоборот
    bottomNavigationBarView.isVisible = true // для скрытия нижней панели навигации
}

fun Fragment.bottomBarVisibilityShow() {
    val bottomNavigationBarView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavBar)
    // если навигация видна то теперь будет не видна и наоборот
    bottomNavigationBarView.isVisible = false // для скрытия нижней панели навигации
}
