package kz.sdk.suevents.fragments

import androidx.navigation.fragment.findNavController
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentChoiceBinding

class ChoiceFragment:BaseFragment<FragmentChoiceBinding>(FragmentChoiceBinding::inflate) {

    override var showBottomNavigation = false
    override fun onBindView() {
        super.onBindView()
        with(binding){
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            createBtn.setOnClickListener {
                findNavController().navigate(R.id.action_choiceFragment_to_adminFragment)
            }
            deleteBtn.setOnClickListener {
                findNavController().navigate(R.id.action_choiceFragment_to_deleteEventFragment)
            }
        }
    }

}