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
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentAdminBinding
import kz.sdk.suevents.models.Event
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class AdminFragment:BaseFragment<FragmentAdminBinding>(FragmentAdminBinding::inflate) {

    private lateinit var selectedDate:String
    private lateinit var selectedTime:String

    private var imageUri: Uri? = null
    @Inject
    lateinit var storageReference: StorageReference

    private val imageResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.img.setImageURI(it)
            imageUri = it
        }
    }


    override fun onBindView() {
        super.onBindView()
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
    private fun saveEventToDatabase(name: String, note: String, imageUrl: String, date:String, time:String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        val eventId = databaseReference.push().key  // Generates a unique id for the event

        val event = Event(title =  name, description = note, img = imageUrl, date = date, time = time)
        eventId?.let {
            databaseReference.child(it).setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
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
