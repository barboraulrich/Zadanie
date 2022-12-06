package com.example.zadanie.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentBarsBinding
import com.example.zadanie.helpers.Injection
import com.example.zadanie.helpers.PreferenceData
import com.example.zadanie.ui.viewmodels.BarsViewModel
import com.example.zadanie.ui.viewmodels.Sort
import com.example.zadanie.ui.viewmodels.data.MyLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class BarsFragment : Fragment() {
    private lateinit var _binding: FragmentBarsBinding
    val binding: FragmentBarsBinding get() = _binding

    private lateinit var _viewmodel: BarsViewModel
    val viewmodel: BarsViewModel get() = _viewmodel

    private lateinit var _fusedLocationClient: FusedLocationProviderClient
    val fusedLocationClient: FusedLocationProviderClient get() = _fusedLocationClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                val action = BarsFragmentDirections.actionBarsFragmentToLocateFragment()
                Navigation.findNavController(requireView()).navigate(action)

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewmodel.show("Only approximate location access granted.")
            }
            else -> {
                viewmodel.show("Location access denied.")
            }
        }
    }

    private val locationPermissionRequestToSortAsc = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                DistanceSort(Sort.ASC_DIST)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewmodel.show("Only approximate location access granted.")
            }
            else -> {
                viewmodel.show("Location access denied.")
            }
        }
    }

    private val locationPermissionRequestToSortDESC = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                DistanceSort(Sort.DESC_DIST)
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewmodel.show("Only approximate location access granted.")
                // Only approximate location access granted.
            }
            else -> {
                viewmodel.show("Location access denied.")
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewmodel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(BarsViewModel::class.java)
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_login)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewmodel
        }.also { bnd ->
            bnd.swiperefresh.setOnRefreshListener {
                viewmodel.refreshData()
            }

            bnd.findBar.setOnClickListener {
                if (checkPermissions()) {
                    val action = BarsFragmentDirections.actionBarsFragmentToLocateFragment()
                    view.findNavController().navigate(action)
                } else {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }

        viewmodel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewmodel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_login)
            }
        }
        setupMenu(view)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupMenu(view: View) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_bars, menu)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        PreferenceData.getInstance().clearData(requireContext())
                        val action = BarsFragmentDirections.actionBarsFragmentToLoginFragment2()
                        view.findNavController().navigate(action)
                        true
                    }
                    R.id.friends -> {
                        val action = BarsFragmentDirections.actionBarsFragmentToFriendsFragment()
                        view.findNavController().navigate(action)
                        true
                    }

                    R.id.sort_aToZ -> {
                        viewmodel.sortList(Sort.ASC_TITLE)
                        true
                    }
                    R.id.sort_zToA -> {
                        viewmodel.sortList(Sort.DESC_TITLE)
                        true
                    }
                    R.id.sort_guests_from_least -> {
                        viewmodel.sortList(Sort.ASC_G)
                        true
                    }
                    R.id.sort_guests_from_most -> {
                        viewmodel.sortList(Sort.DESC_G)
                        true
                    }

                    R.id.sort_distance_from_least -> {

                        if (checkPermissions()) {
                            DistanceSort(Sort.ASC_DIST)

                        } else {
                            locationPermissionRequestToSortAsc.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                            viewmodel.show("Permissions have not been granted.")
                        }

                        true
                    }
                    R.id.sort_distance_from_most -> {

                        if (checkPermissions()) {
                            DistanceSort(Sort.DESC_DIST)
                        } else {
                            locationPermissionRequestToSortDESC.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                            viewmodel.show("Permissions have not been granted.")
                        }

                        true
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

//locatefragment LOADDATA()
    @SuppressLint("MissingPermission")
    private fun DistanceSort(sort: Sort) {
        if (checkPermissions()) {
            viewmodel.loading.postValue(true)
            fusedLocationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    viewmodel.myLocation.postValue(MyLocation(it.latitude, it.longitude))
                    viewmodel.sortList(sort)
                } ?: viewmodel.loading.postValue(false)
            }
        }
    }
}

