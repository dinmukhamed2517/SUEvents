package kz.sdk.suevents.fragments

import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.adapters.EventAdapter
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentSearchBinding
import kz.sdk.suevents.models.Event

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private lateinit var adapter: EventAdapter
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Events")

    override fun onBindView() {
        super.onBindView()
        adapter = EventAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        loadProducts()

        adapter.itemClick = { event ->
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToEventDetailsFragment(event)
            )
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = binding.editText.text.toString()
                if (searchText.isNotEmpty()) {
                    searchProduct(searchText)
                } else {
                    Toast.makeText(requireContext(), "Введите название события", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
    }

    private fun loadProducts() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Event>()
                snapshot.children.forEach { dataSnapshot ->
                    dataSnapshot.getValue(Event::class.java)?.let { product ->
                        products.add(product)
                    }
                }
                adapter.submitList(products)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchProduct(input: String) {
        databaseReference.orderByChild("title").startAt(input).endAt(input + "\uf8ff")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchResults = mutableListOf<Event>()
                    snapshot.children.forEach { dataSnapshot ->
                        dataSnapshot.getValue(Event::class.java)?.let { product ->
                            searchResults.add(product)
                        }
                    }
                    adapter.submitList(searchResults)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Search failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}