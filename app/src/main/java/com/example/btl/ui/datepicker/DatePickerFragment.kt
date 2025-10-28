package com.example.btl.ui.datepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btl.databinding.FragmentDatePickerBinding

class DatePickerFragment : Fragment() {

    private var _binding: FragmentDatePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle close button click to navigate back
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle OK button click to navigate back
        binding.okButton.setOnClickListener {
            // Here you would typically pass the selected dates back to the previous fragment
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
