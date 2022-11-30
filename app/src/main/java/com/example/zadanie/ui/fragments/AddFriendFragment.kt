package com.example.zadanie.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentAddFriendBinding
import com.example.zadanie.helpers.Injection
import com.example.zadanie.helpers.PreferenceData
import com.example.zadanie.ui.viewmodels.AddFriendViewModel


class AddFriendFragment : Fragment() {

    private lateinit var _binding: FragmentAddFriendBinding
    val binding : FragmentAddFriendBinding get() = _binding

    private lateinit var _addFriendViewModel: AddFriendViewModel
    val addFriendViewModel: AddFriendViewModel get() = _addFriendViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _addFriendViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(AddFriendViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_bars)
            return
        }

        binding.addNewFriend.setOnClickListener {
            addFriend()
        }

        addFriendViewModel.isAdded.observe(viewLifecycleOwner){
            if(it)
            {
                view.findNavController().navigateUp()
            }
        }
    }

    private fun addFriend()
    {
        val friendUsername = binding.addFriendName.text.toString()

        if(friendUsername.isBlank())
        {
            println("prazdne")
            addFriendViewModel.show("Fill username")
            return
        }

        addFriendViewModel.addFriend(friendUsername)

    }



}