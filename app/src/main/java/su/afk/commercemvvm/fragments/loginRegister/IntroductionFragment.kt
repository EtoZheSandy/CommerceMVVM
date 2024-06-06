package su.afk.commercemvvm.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.activities.ShoppingActivity
import su.afk.commercemvvm.databinding.FragmentAccountOptionsBinding
import su.afk.commercemvvm.databinding.FragmentIntroductionBinding
import su.afk.commercemvvm.viewModels.IntoDuctionViewModel

@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntoDuctionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch  {
            viewModel.navigate.collect{
                when(it) {
                    IntoDuctionViewModel.SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            // для стека приложения, если юзеры уже вошли то при нажатие назат им не нужно будет возвращаться на эту страницу
                            // так что этот флажок гарантирует удаление этого активити
                            startActivity(intent)
                            // Переход к ShoppingActivity должен завершать текущую активность (requireActivity().finish()),
                            // чтобы избежать возврата к фрагментам входа
                            requireActivity().finish()
                        }
                    }

                    IntoDuctionViewModel.ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
                    }

                    else -> Unit
                }
            }
        }


        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick() // сохраняем клик
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}