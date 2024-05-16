package kz.sdk.suevents.fragments

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentProfileBinding
import kz.sdk.suevents.firebase.UserDao
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment:BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var userDao: UserDao
    override fun onBindView() {
        super.onBindView()
        userDao.getData()

        with(binding){
            adminBtn.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_choiceFragment)
            }
        }
        userDao.getDataLiveData.observe(this){
            if(it?.isAdmin == false){
                binding.adminBtn.isVisible = false
            }

            binding.signOutBtn.setOnClickListener {
                signOut()
            }
            binding.ava.setOnClickListener{
                findNavController().navigate(
                    R.id.action_profileFragment_to_updateProfileFragment
                )
            }
            binding.map.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_mapFragment)
            }
            binding.about.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)
            }

            binding.support.setOnClickListener{
                findNavController().navigate(R.id.action_profileFragment_to_helpFragment)
            }
            binding.websiteBtn.setOnClickListener {
                openConferenceUrl()
            }
            binding.name.text = it?.name
            binding.email.text = firebaseAuth.currentUser?.email
            if (it?.pictureUrl != null) {
                Glide.with(requireContext())
                    .load(it?.pictureUrl)
                    .into(binding.ava)
            } else {
                binding.ava.setImageResource(R.drawable.profile_icon)
            }
        }
    }

    private fun openConferenceUrl() {
        val url = "https://satbayev.university/ru"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser == null){
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

    }
    private fun signOut() {
        var alertDialog: AlertDialog? = null
        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                firebaseAuth.signOut()
                alertDialog?.dismiss()
                findNavController().navigate(
                    R.id.action_profileFragment_to_loginFragment
                )
            }
            .setNegativeButton("Отмена") { _, _ ->
                alertDialog?.dismiss()
            }
            .show()
    }

}