package su.afk.commercemvvm.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.commercemvvm.R
import su.afk.commercemvvm.adapters.AllOrdersAdapter
import su.afk.commercemvvm.databinding.FragmentOrdersBinding
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.viewModels.AllOrdersViewModel

@AndroidEntryPoint
class AllOrdersFragment: Fragment(R.layout.fragment_orders) {
    private lateinit var binding: FragmentOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val ordersAdapter by lazy { AllOrdersAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()

        lifecycleScope.launch {
            viewModel.allOrders.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        ordersAdapter.differ.submitList(it.data) // отправляем список с нашими заказами в адаптер

                        if(it.data.isNullOrEmpty()) { // если заказов не было то выведем заготовку
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // при клике по заказу ловим его тут и передаем в args по навигации во фрагмент OrderDetailFragment
        ordersAdapter.onClick = {
            // стрелка перехода в навигации от одного фрагмента к другому, в аргументы передаем order из адаптера
            val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(order = it)
            findNavController().navigate(action) // делаем переданное действие
        }
    }

    private fun setupRv() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


}