package su.afk.commercemvvm.fragments.shopping

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.data.models.Address
import su.afk.commercemvvm.databinding.FragmentAddressBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.AddressViewModel

@AndroidEntryPoint
class AddressFragment: Fragment(R.layout.fragment_address) {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // для добавления адреса
        lifecycleScope.launch {
            viewModel.addNewAddress.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp() // возвращаемся назад
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // для вывода ошибок заполнения
        lifecycleScope.launch {
            viewModel.errorAddNewAddress.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
                val address = Address(
                    addressTitle = edAddressTitle.text.toString(),
                    fullName = edFullName.text.toString(),
                    state = edState.text.toString(),
                    phone = edPhone.text.toString(),
                    city = edCity.text.toString(),
                    street = edStreet.text.toString()
                )
                // добавляем адрес
                viewModel.addAddress(address = address)
            }
        }
    }
}