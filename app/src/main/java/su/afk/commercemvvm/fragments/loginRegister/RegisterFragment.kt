package su.afk.commercemvvm.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.User
import su.afk.commercemvvm.databinding.FragmentRegisterBinding
import su.afk.commercemvvm.util.RegisterFailedState
import su.afk.commercemvvm.util.RegisterValidation
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.RegisterViewModel

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHaveAccount.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            buttonRegisterAccount.setOnClickListener {
                val user = User(
                    firstName = edRegisterFirstName.text.toString().trim(),
                    lastName = edRegisterLastName.text.toString().trim(),
                    email = edRegisterEmail.text.toString().trim(),
                )
                val password = edRegisterPassword.text.toString()

                viewModel.createAccountWithEmailAndPassword(user = user, password = password)
            }
        }

        lifecycleScope.launch {
            viewModel.register.collect{
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterAccount.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("RegisterFragment", "${it.message}")
                        binding.buttonRegisterAccount.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.e("RegisterFragment", "${it.message}")
                        binding.buttonRegisterAccount.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        // для ошибок ввода
        lifecycleScope.launch {
            viewModel.validation.collect{
                if(it.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edRegisterEmail.apply {
                            requestFocus()
                            error = it.email.message
                        }
                    }
                }
                if (it.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edRegisterPassword.apply {
                            requestFocus()
                            error = it.password.message
                        }
                    }
                }
            }
        }
    }
}