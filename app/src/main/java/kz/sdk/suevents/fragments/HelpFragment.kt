package kz.sdk.suevents.fragments

import androidx.navigation.fragment.findNavController
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentHelpBinding

class HelpFragment:BaseFragment<FragmentHelpBinding>(FragmentHelpBinding::inflate) {
    override fun onBindView() {
        super.onBindView()

        showBottomNavigation = false
        with(binding){
            backBtn.setOnClickListener {
                findNavController().popBackStack()

            }

            confirmButton.setOnClickListener {
                if(etLetter.text.toString().isEmpty()){
                    showCustomCancelDialog("Ошибка", "Пожалуйста заполните письмо")
                    tilLetter.isErrorEnabled = true
                    tilLetter.error = "Заполните"
                }
                else{
                    tilLetter.isErrorEnabled = false
                    showCustomDialog("Успех", "Ваше письмо отправлено в министерство. Спасибо за помощь!")
                }
            }
        }
    }


}