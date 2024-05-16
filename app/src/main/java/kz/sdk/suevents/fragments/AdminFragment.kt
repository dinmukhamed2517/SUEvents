package kz.sdk.suevents.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.adapters.FilterAdapter
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentAdminBinding
import kz.sdk.suevents.models.Event
import kz.sdk.suevents.models.Filter
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class AdminFragment:BaseFragment<FragmentAdminBinding>(FragmentAdminBinding::inflate) {

    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    override var showBottomNavigation = false
    private lateinit var filterAdapter: FilterAdapter
    private var selectedFilterTitle: String? = null
    private var selectedCategory: String? = null
    private lateinit var categoryAdapter: FilterAdapter

    private var imageUri: Uri? = null

    @Inject
    lateinit var storageReference: StorageReference

    private val imageResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.img.setImageURI(it)
            imageUri = it
            binding.textImg.isVisible = false
        }
    }

    private fun setupRecyclerView() {
        filterAdapter = FilterAdapter().apply {
            itemClick = { filter ->
                selectedFilterTitle = filter.title
                Toast.makeText(context, "Выбрано: ${filter.title}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }

        val filters = listOf(
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
        filterAdapter.submitList(filters)
    }

    private fun setupCategoryRecycler() {
        categoryAdapter = FilterAdapter().apply {
            itemClick = { filter ->
                selectedCategory = filter.title
                Toast.makeText(context, "Выбрано: ${filter.title}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.categoryRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        val categories = listOf(
            Filter(10, "Олимпиады"),
            Filter(11, "Конференций"),
            Filter(12, "Семинары"),
        )
        categoryAdapter.submitList(categories)
    }




    override fun onBindView() {
        super.onBindView()
        setupRecyclerView()
        setupCategoryRecycler()
        binding.dateBtn.setOnClickListener {
            showDatePickerDialog(requireContext()){year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.selectedDate.text = selectedDate
            }
        }
        binding.timeBtn.setOnClickListener {
            showTimePickerDialog(requireContext()){hourOfDay, minute ->
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                binding.selectedTime.text = selectedTime
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.uploadImg.setOnClickListener {
            selectEventImage()
        }
        binding.createBtn.setOnClickListener {
            if (binding.nameInput.text.isNullOrEmpty() || binding.noteInput.text.isNullOrEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage { imageUrl ->
                    saveEventToDatabase(binding.nameInput.text.toString(), binding.noteInput.text.toString(), imageUrl, selectedDate, selectedTime)
                }
            }
        }

    }

    fun selectEventImage() {
        imageResultLauncher.launch("image/*")
    }
    private fun uploadImage(callback: (String) -> Unit) {
        imageUri?.let { uri ->
            binding.img.setImageURI(uri)
            val ref = storageReference.child(uri.lastPathSegment ?: "temp")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    callback(downloadUri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveEventToDatabase(name: String, note: String, imageUrl: String, date: String, time: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        val eventId = databaseReference.push().key
        val event = Event(
            id = eventId,
            title = name,
            description = note,
            img = imageUrl,
            date = date,
            time = time,
            type = selectedFilterTitle,
            category = selectedCategory,
        )
        eventId?.let {
            databaseReference.child(it).setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showCustomDialog("Успех", "Событие создано успешно!")
                } else {
                    Toast.makeText(context, "Failed to create event: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun showDatePickerDialog(context: Context, onDateSet: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            onDateSet(selectedYear, selectedMonth, selectedDayOfMonth)
        },
        year,
        month,
        day
    )

    datePickerDialog.show()
}



fun showTimePickerDialog(context: Context, onTimeSet: (hourOfDay: Int, minute: Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHourOfDay, selectedMinute ->
            onTimeSet(selectedHourOfDay, selectedMinute)
        },
        hour,
        minute,
        true
    )

    timePickerDialog.show()
}
