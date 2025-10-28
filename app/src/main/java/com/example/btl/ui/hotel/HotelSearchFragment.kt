package com.example.btl.ui.hotel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btl.R
import com.example.btl.databinding.FragmentHotelSearchBinding
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HotelSearchFragment : Fragment() {

    private var _binding: FragmentHotelSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotelSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Navigate to Location Search
        binding.locationSearchText.setOnClickListener {
            findNavController().navigate(R.id.action_hotelSearchFragment_to_locationSearchFragment)
        }

        val dateClickListener = View.OnClickListener {
            showDateRangePicker()
        }

        binding.checkInDateText.setOnClickListener(dateClickListener)
        binding.checkOutDateText.setOnClickListener(dateClickListener)
    }

    private fun showDateRangePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val tomorrow = today + TimeUnit.DAYS.toMillis(1)

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Chọn ngày")
            .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
            .setSelection(Pair(today, tomorrow))
            .build()

        dateRangePicker.show(parentFragmentManager, "DATE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            binding.checkInDateText.text = convertUtcLongToDate(startDate)
            binding.checkOutDateText.text = convertUtcLongToDate(endDate)

            val nights = TimeUnit.MILLISECONDS.toDays(endDate - startDate)
            Toast.makeText(requireContext(), "Đã chọn $nights đêm", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertUtcLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("EEE, dd MMM", Locale("vi"))
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
