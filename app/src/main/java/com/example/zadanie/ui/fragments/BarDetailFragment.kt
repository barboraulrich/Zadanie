package com.example.zadanie.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentDetailBarBinding
import com.example.zadanie.helpers.Injection
import com.example.zadanie.helpers.PreferenceData
import com.example.zadanie.ui.viewmodels.DetailViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BarDetailFragment : Fragment() {
    private lateinit var _binding: FragmentDetailBarBinding
    val binding: FragmentDetailBarBinding get() = _binding

    private lateinit var _viewModel: DetailViewModel
    val viewModel: DetailViewModel get() = _viewModel

    private val _navigationArgs: BarDetailFragmentArgs by navArgs()
    val navigationArgs: BarDetailFragmentArgs get() = _navigationArgs

    private lateinit var mapFragment : SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_login)
            return
        }

        viewModel.bar.observe(viewLifecycleOwner){
            bar-> if (bar!= null) {
            mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
            mapFragment?.getMapAsync { googleMap ->
                addMarkers(googleMap,LatLng(bar.lat, bar.lon))
            }
            }
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel

        }.also { bnd -> //podmnozina buttonov

            viewModel.getBarItem(navigationArgs.id).observe(viewLifecycleOwner)
            {
                it?.let {
                    binding.quantity.text = it.users.toString()
                }
            }

            viewModel.details.observe(viewLifecycleOwner){
                it?.let {
                    bnd.openingHours.isVisible = false
                    for(item in it)
                    {
                        println(item.key)
                        if(item.key == "opening_hours")
                        {
                            bnd.openingHours.isVisible = true
                            bnd.openingHours.text = item.value.replace(";","\n")
                        }
                    }
                }
            }
            viewModel.details.observe(viewLifecycleOwner){
                it?.let {
                    if(!it.isEmpty())
                    {
                        var isWebsitePresent = false
                        for(item in it)
                        {
                            if(item.key == "website")
                            {
                                println(item.key)
                                println(item.value)
                                bnd.web.isVisible = true
                                bnd.web.setOnClickListener{
                                    val queryUrl: Uri = Uri.parse(item.value)
                                    val intent = Intent(Intent.ACTION_VIEW, queryUrl)
                                    startActivity(intent)
                                }
                                isWebsitePresent = true
                            }
                        }
                        if(!isWebsitePresent)
                            bnd.web.isVisible = false
                    }
                    else
                    {
                        bnd.web.isVisible = false
                    }
                }
            }

            bnd.mapButton.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "geo:0,0,?q=" +
                                    "${viewModel.bar.value?.lat ?: 0}," +
                                    "${viewModel.bar.value?.lon ?: 0}" +
                                    "(${viewModel.bar.value?.name ?: ""}"
                        )
                    )
                )
            }
        }

        viewModel.bar.observe(viewLifecycleOwner){
            it?.let {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = it.name
            }
        }


        viewModel.loadBar(navigationArgs.id)
    }

    private fun addMarkers(googleMap: GoogleMap, latLng : LatLng) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng )
            )
    }

}