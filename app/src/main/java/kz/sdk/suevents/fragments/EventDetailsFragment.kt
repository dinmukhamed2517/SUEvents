package kz.sdk.suevents.fragments

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentEventDetailsBinding
import kz.sdk.suevents.firebase.UserDao
import javax.inject.Inject


@AndroidEntryPoint
class EventDetailsFragment:BaseFragment<FragmentEventDetailsBinding>(FragmentEventDetailsBinding::inflate) {
    private val args:EventDetailsFragmentArgs by navArgs()
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onBindView() {
        super.onBindView()
        val event = args.event
        with(binding){
            title.text = event.title
            Glide.with(requireContext())
                .load(event.img)
                .placeholder(R.drawable.placeholder_event)
                .into(img)
            description.text = event.description

            addToFavorites.setOnClickListener {
                userDao.saveEventToList(event)
                showCustomDialog("Успех", "Вы сохранили cобытие в избранное")

            }
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            goToConf.setOnClickListener{
                openConferenceUrl()
            }

        }

    }
    private fun openConferenceUrl() {
        val url = "https://satbayev.university/ru"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}