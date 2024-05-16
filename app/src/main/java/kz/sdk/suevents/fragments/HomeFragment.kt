package kz.sdk.suevents.fragments

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.adapters.EventAdapter
import kz.sdk.suevents.adapters.FilterAdapter
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentHomeBinding
import kz.sdk.suevents.models.Event
import kz.sdk.suevents.models.Filter

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var adapter: EventAdapter
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var categoryAdapter: FilterAdapter

    private var selectedFilterTitle: String? = null
    private var selectedCategoryTitle:String? = null

    override fun onBindView() {


        super.onBindView()
        adapter = EventAdapter()
        filterAdapter =  FilterAdapter()

        categoryAdapter = FilterAdapter()
        categoryAdapter.submitList(getCategories())
        filterAdapter.submitList(getFilters())
        loadEvents()

        with(binding) {
            eventRecycler.adapter = adapter
            eventRecycler.layoutManager = LinearLayoutManager(requireContext())
            filterRecycler.adapter = filterAdapter
            filterRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            categoryRecycler.adapter = categoryAdapter
            categoryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        adapter.itemClick = {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(it))
        }
        binding.searchBtn.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        filterAdapter.itemClick = {
            selectedFilterTitle = it.title
            loadEvents()
        }
        categoryAdapter.itemClick = {
            selectedCategoryTitle = it.title
            loadEvents()
        }

    }
    private fun matchesFilter(event: Event): Boolean {
        val matchesType = selectedFilterTitle == null || event.type == selectedFilterTitle
        val matchesCategory = selectedCategoryTitle == null || event.category == selectedCategoryTitle

        return matchesType && matchesCategory
    }

    private fun loadEvents() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                snapshot.children.forEach { dataSnapshot ->
                    val event = dataSnapshot.getValue(Event::class.java)
                    event?.let {
                        if (matchesFilter(it)) {
                            events.add(it)
                        }
                    }
                }
                adapter.submitList(events)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFilters():List<Filter>{
        return listOf(
            Filter(1, "ИГиНД"),
            Filter(2, "ГМИ"),
            Filter(3, "ИАиИТ"),
            Filter(4, "ИЭиМ"),
            Filter(5, "ИАиС"),
            Filter(6, "ШТИиЛ"),
            Filter(7, "ИУП"),
            Filter(8, "ИВД"),
            Filter(9, "ИЦТиПР"),
        )
    }
    private fun getCategories():List<Filter>{
        return listOf(
            Filter(10, "Олимпиады"),
            Filter(11, "Конференций"),
            Filter(12, "Семинары"),
        )
    }
}