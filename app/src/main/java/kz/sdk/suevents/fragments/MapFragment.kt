package kz.sdk.suevents.fragments

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseFragment
import kz.sdk.suevents.databinding.FragmentMapBinding

class MapFragment:BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    override var showBottomNavigation = false

    override fun onBindView() {
        super.onBindView()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.zoomIn.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }

        binding.zoomOut.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val zoomLevel = 17.0f
        val latLng = LatLng(43.237302825164356, 76.93136472533305)
        mMap.addMarker(MarkerOptions().position(latLng!!).title("Satbaev University"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

    }

}