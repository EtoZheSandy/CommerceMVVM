package su.afk.commercemvvm.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.activities.LoginRegisterActivity
import su.afk.commercemvvm.databinding.FragmentProfileBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.bottomBarVisibilityShow
import su.afk.commercemvvm.viewModels.ProfileVieModel

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileVieModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // изменение информации о профиле
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        // все заказы
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }

        // способы оплаты
        binding.linearBilling.setOnClickListener {
            // Для передачи аргументов через навигацию
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                totalPrice = 0f,
                products = emptyArray(),
                payment = false)
            findNavController().navigate(action) // передаем действие в контроллер
        }

        // выход из системы
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            // переходим к экрану входа
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent) // запускаем намерение
            requireActivity().finish() // закрываем это активити
        }

        // версия приложения
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_NAME}"


        lifecycleScope.launch {
            viewModel.user.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.photoUrl).error(ColorDrawable(Color.BLACK))
                            .into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomBarVisibilityShow() // возвращаем нижний бар
    }
}