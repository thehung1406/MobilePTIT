package com.example.btl.ui.hotel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.R
import com.example.btl.adapter.HotelAdapter
import com.example.btl.databinding.FragmentHotelSearchBinding
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HotelSearchFragment : Fragment() {

    private var _binding: FragmentHotelSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HotelSearchViewModel by viewModels()

    private lateinit var hotelAdapter: HotelAdapter

    private var selectedLocation: String = ""
    private var checkInDate: Long = 0
    private var checkOutDate: Long = 0
    private var numberOfGuests: Int = 2
    private var numberOfRooms: Int = 1

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

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        setDefaultDates()
        updateGuestsRoomsText()

        // Load tất cả hotels ngay khi vào màn hìn
    }


    private fun setupRecyclerView() {
        hotelAdapter = HotelAdapter(
            onItemClick = { property ->
                Log.d("HotelSearchFragment", "Clicked on hotel: ${property.name}")

                // Truyền đầy đủ thông tin sang màn hình chi tiết
                val bundle = Bundle().apply {
                    putInt("propertyId", property.id ?: 0)
                    putString("propertyName", property.name)
                    putLong("checkInDate", checkInDate)
                    putLong("checkOutDate", checkOutDate)
                    putInt("numberOfGuests", numberOfGuests)
                    putInt("numberOfRooms", numberOfRooms)
                }

                // Navigate to hotel detail
                try {
                    findNavController().navigate(
                        R.id.action_hotelSearchFragment_to_hotelDetailFragment,
                        bundle
                    )
                } catch (e: Exception) {
                    Log.e("HotelSearchFragment", "Navigation error: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        "Chi tiết: ${property.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding.recyclerViewHotels.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hotelAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.locationSearchText.setOnClickListener {
            showLocationInputDialog()
        }

        val dateClickListener = View.OnClickListener {
            showDateRangePicker()
        }

        binding.checkInDateText.setOnClickListener(dateClickListener)
        binding.checkOutDateText.setOnClickListener(dateClickListener)
        binding.dateContainer?.setOnClickListener(dateClickListener)

        binding.guestsRoomsText.setOnClickListener {
            showGuestsRoomsDialog()
        }

        // Nút tìm kiếm
        binding.searchButton.setOnClickListener {
            performSearch()
        }

        binding.filterButton?.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun setDefaultDates() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val tomorrow = today + TimeUnit.DAYS.toMillis(1)
        checkInDate = today
        checkOutDate = tomorrow
        updateDateTexts()
    }

    private fun updateDateTexts() {
        binding.checkInDateText.text = convertUtcLongToDate(checkInDate)
        binding.checkOutDateText.text = convertUtcLongToDate(checkOutDate)
        val nights = TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate)
        binding.nightsText?.text = "$nights đêm"
    }

    private fun updateGuestsRoomsText() {
        binding.guestsRoomsText.text = "$numberOfGuests khách, $numberOfRooms phòng"
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Chọn ngày nhận và trả phòng")
            .setSelection(Pair(checkInDate, checkOutDate))
            .build()

        dateRangePicker.show(parentFragmentManager, "DATE_PICKER")
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            checkInDate = selection.first
            checkOutDate = selection.second
            updateDateTexts()
        }
    }

    private fun showLocationInputDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val input = android.widget.EditText(requireContext())
        input.hint = "Nhập tên thành phố"
        input.setText(selectedLocation)

        builder.setTitle("Chọn địa điểm")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                selectedLocation = input.text.toString()
                binding.locationSearchText.text = if (selectedLocation.isEmpty()) {
                    "Chọn địa điểm"
                } else {
                    selectedLocation
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showGuestsRoomsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_guests_rooms, null)

        // ✅ SỬA: Đổi từ ImageButton sang Button hoặc MaterialButton
        val guestsText = dialogView.findViewById<android.widget.TextView>(R.id.guestsCountText)
        val roomsText = dialogView.findViewById<android.widget.TextView>(R.id.roomsCountText)
        val guestsPlus = dialogView.findViewById<android.widget.Button>(R.id.guestsPlusButton)  // ✅ Button thay vì ImageButton
        val guestsMinus = dialogView.findViewById<android.widget.Button>(R.id.guestsMinusButton) // ✅ Button thay vì ImageButton
        val roomsPlus = dialogView.findViewById<android.widget.Button>(R.id.roomsPlusButton)    // ✅ Button thay vì ImageButton
        val roomsMinus = dialogView.findViewById<android.widget.Button>(R.id.roomsMinusButton)  // ✅ Button thay vì ImageButton

        var tempGuests = numberOfGuests
        var tempRooms = numberOfRooms

        guestsText.text = tempGuests.toString()
        roomsText.text = tempRooms.toString()

        guestsPlus.setOnClickListener {
            if (tempGuests < 10) {
                tempGuests++
                guestsText.text = tempGuests.toString()
            }
        }

        guestsMinus.setOnClickListener {
            if (tempGuests > 1) {
                tempGuests--
                guestsText.text = tempGuests.toString()
            }
        }

        roomsPlus.setOnClickListener {
            if (tempRooms < 5) {
                tempRooms++
                roomsText.text = tempRooms.toString()
            }
        }

        roomsMinus.setOnClickListener {
            if (tempRooms > 1) {
                tempRooms--
                roomsText.text = tempRooms.toString()
            }
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Chọn số khách và phòng")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { _, _ ->
                numberOfGuests = tempGuests
                numberOfRooms = tempRooms
                updateGuestsRoomsText()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showFilterDialog() {
        Toast.makeText(requireContext(), "Bộ lọc đang phát triển", Toast.LENGTH_SHORT).show()
    }

    private fun performSearch() {
        Log.d("HotelSearchFragment", "performSearch called")
        Log.d("HotelSearchFragment", "selectedLocation: '$selectedLocation'")

        if (selectedLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Tìm kiếm tất cả khách sạn...", Toast.LENGTH_SHORT).show()
        }

        viewModel.searchHotels(
            location = selectedLocation,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            guests = numberOfGuests,
            rooms = numberOfRooms
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        Log.d("HotelSearchFragment", "Loading: $isLoading")
                    }
                }

                // Observe search results
                launch {
                    viewModel.searchResults.collect { properties ->
                        Log.d("HotelSearchFragment", "Received ${properties.size} properties")
                        hotelAdapter.submitList(properties)

                        // Show/hide empty state
                        if (viewModel.hasSearched.value) {
                            binding.recyclerViewHotels.visibility =
                                if (properties.isNotEmpty()) View.VISIBLE else View.GONE
                            binding.emptyStateText?.visibility =
                                if (properties.isEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }

                // Observe errors
                launch {
                    viewModel.error.collect { errorMessage ->
                        if (errorMessage.isNotEmpty()) {
                            Log.e("HotelSearchFragment", "Error: $errorMessage")
                            binding.emptyStateText?.text = errorMessage
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
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
