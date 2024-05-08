package kz.sdk.suevents.fragments

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.sdk.suevents.adapters.EventAdapter
import kz.sdk.suevents.adapters.NotificationAdapter
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentNotificationsBinding
import kz.sdk.suevents.models.Event

class NotificationFragment:BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {
    private lateinit var adapter: NotificationAdapter

    override fun onBindView() {
        super.onBindView()
        adapter = NotificationAdapter()
        with(binding) {
            notificationRecycler.adapter = adapter
            notificationRecycler.layoutManager = LinearLayoutManager(requireContext())
        }
        loadEvents()
    }

    private fun loadEvents() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                snapshot.children.forEach {
                    val event = it.getValue(Event::class.java)
                    event?.let { events.add(it) }
                }
                adapter.submitList(events)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}