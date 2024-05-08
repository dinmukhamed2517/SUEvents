package kz.sdk.suevents.fragments

import android.system.Os.remove
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.adapters.FavoritesAdapter
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentFavoritesBinding
import kz.sdk.suevents.firebase.UserDao
import kz.sdk.suevents.models.Event
import javax.inject.Inject




@AndroidEntryPoint
class FavoritesFragment:BaseFragment<FragmentFavoritesBinding>(FragmentFavoritesBinding::inflate) {
    var products: MutableList<Event> = mutableListOf()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var userDao: UserDao
    override fun onBindView() {
        userDao.getData()
        super.onBindView()

        val adapter = FavoritesAdapter()

        adapter.itemClick = {
            findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToEventDetailsFragment(it)
            )
        }
        with(binding){
            startBtn.setOnClickListener {
                findNavController().navigate(R.id.action_favoritesFragment_to_homeFragment)
            }

            cartRecycler.adapter = adapter
            cartRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            adapter.deleteButtonClicked = { product ->
                val keyToDelete = userDao.getDataLiveData.value?.favorites?.filterValues { it.id == product.id }?.keys?.firstOrNull()
                keyToDelete?.let { key ->
                    userDao.deleteProductFromList(key)
                    val updatedProducts = ArrayList(products).apply {
                        remove(product)
                    }
                    adapter.submitList(updatedProducts)
                    products = updatedProducts
                } ?: run {
                    Log.e("CartFragment", "Failed to find key for product deletion")
                }
            }
        }
        userDao.getDataLiveData.observe(viewLifecycleOwner) { userData ->
            products.clear()
            userData?.favorites?.values?.let { productList ->
                products.addAll(productList)
            }
            adapter.submitList(products.toList())

            val isCartEmpty = products.isEmpty()
            binding.emptyCartCv.isVisible = isCartEmpty
            binding.cartRecycler.isVisible = !isCartEmpty
        }
    }
    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser == null) {
            findNavController().navigate(R.id.action_favoritesFragment_to_loginFragment)
        }

    }

}