package kz.sdk.suevents.fragments

import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentAboutBinding

class AboutFragment:BaseFragment<FragmentAboutBinding>(FragmentAboutBinding::inflate) {

    override var showBottomNavigation = false
    override fun onBindView() {
        super.onBindView()

    }
}