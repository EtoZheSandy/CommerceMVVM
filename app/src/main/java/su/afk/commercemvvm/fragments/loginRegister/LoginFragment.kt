package su.afk.commercemvvm.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.activities.ShoppingActivity
import su.afk.commercemvvm.databinding.FragmentLoginBinding
import su.afk.commercemvvm.dialogs.setupBottomDialog
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.LoginViewModel
import su.afk.commercemvvm.viewModels.RegisterViewModel

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNotHaveAccount.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // авторизация
        binding.buttonRegisterAccount.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email = email, password = password)
        }

        // для сброса пароля
        binding.tvLoginForgotPassword.setOnClickListener {
            setupBottomDialog { email ->
                viewModel.resetPasswordEmail(email = email)
            }
        }

        // для вывода ошибки сброса пароля
        lifecycleScope.launch {
            viewModel.resetPassword.collect{
                when(it) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Письмо отправлено на вашу почту", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // для авторизации
        lifecycleScope.launch {
            viewModel.login.collect{
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterAccount.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonRegisterAccount.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            // для стека приложения, если юзеры уже вошли то при нажатие назат им не нужно будет возвращаться на эту страницу
                            // так что этот флажок гарантирует удаление этого активити
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        binding.buttonRegisterAccount.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}