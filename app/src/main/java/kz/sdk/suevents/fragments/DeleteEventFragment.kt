package kz.sdk.suevents.fragments

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
import kz.sdk.suevents.databinding.FragmentDeleteEventBinding
import kz.sdk.suevents.models.Event


@AndroidEntryPoint
class DeleteEventFragment:BaseFragment<FragmentDeleteEventBinding>(FragmentDeleteEventBinding::inflate) {
    private lateinit var eventAdapter: EventAdapter
    override var showBottomNavigation = false

    override fun onBindView() {
        super.onBindView()

        eventAdapter = EventAdapter()
        eventAdapter.itemClick = {
            deleteEvent(it)
        }

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        loadEvents()
    }
    private fun loadEvents() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                snapshot.children.forEach { dataSnapshot ->
                    dataSnapshot.getValue(Event::class.java)?.let { event ->
                        events.add(event)
                    }
                }
                eventAdapter.submitList(events)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteEvent(event: Event) {
        event.id?.let {
            FirebaseDatabase.getInstance().getReference("Events")
                .child(it)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showCustomDialog("Успех", "Событие удалено успешно")

                    } else {
                        Toast.makeText(context, "Failed to delete event: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}