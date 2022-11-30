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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentDetailBarBinding
import com.example.zadanie.helpers.Injection
import com.example.zadanie.helpers.PreferenceData
import com.example.zadanie.ui.viewmodels.DetailViewModel

class BarDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBarBinding
    private lateinit var viewModel: DetailViewModel
    private val navigationArgs: BarDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBarBinding.inflate(inflater, container, false)
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
            model = viewModel
        }.also { bnd ->

            viewModel.getBarItem(navigationArgs.id).observe(viewLifecycleOwner)
            {
                it?.let {
                    binding.counter.text = it.users.toString()
                }
            }

            viewModel.details.observe(viewLifecycleOwner){
                it?.let {
                    bnd.phoneNumber.isVisible = false
                    for(item in it)
                    {
                        if(item.key == "phone")
                        {
                            bnd.phoneNumber.isVisible = true
                            bnd.phoneNumber.text = item.value
                            break
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
                                bnd.web.isEnabled = true
                                bnd.web.setOnClickListener{
                                    val queryUrl: Uri = Uri.parse(item.value)
                                    val intent = Intent(Intent.ACTION_VIEW, queryUrl)
                                    startActivity(intent)
                                }
                                isWebsitePresent = true
                                break
                            }
                        }

                        if(!isWebsitePresent)
                            bnd.web.isEnabled = false
                    }
                    else
                    {
                        bnd.web.isEnabled = false
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

}