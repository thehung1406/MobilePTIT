package com.example.btl.ui.hotel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.btl.adapter.RoomTypeAdapter
import com.example.btl.databinding.FragmentHotelDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HotelDetailFragment : Fragment() {

    private var _binding: FragmentHotelDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HotelDetailViewModel by viewModels()

    private lateinit var roomTypeAdapter: RoomTypeAdapter

    private var propertyId: Int = 0
    // THÊM: Nhận thông tin từ search
    private var checkInDate: Long = 0
    private var checkOutDate: Long = 0
    private var numberOfGuests: Int = 2
    private var numberOfRooms: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotelDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get all arguments
        propertyId = arguments?.getInt("propertyId", 0) ?: 0
        checkInDate = arguments?.getLong("checkInDate", System.currentTimeMillis()) ?: System.currentTimeMillis()
        checkOutDate = arguments?.getLong("checkOutDate", System.currentTimeMillis()) ?: System.currentTimeMillis()
        numberOfGuests = arguments?.getInt("numberOfGuests", 2) ?: 2
        numberOfRooms = arguments?.getInt("numberOfRooms", 1) ?: 1

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        if (propertyId > 0) {
            viewModel.loadPropertyDetail(propertyId)
            viewModel.loadRoomTypes(propertyId)
        }
    }

    private fun setupRecyclerView() {
        roomTypeAdapter = RoomTypeAdapter(
            onItemClick = { roomType ->
                // Truyền đầy đủ thông tin sang màn hình booking
                val bundle = Bundle().apply {
                    putInt("roomTypeId", roomType.id ?: 0)
                    putInt("propertyId", propertyId)
                    putString("roomTypeName", roomType.name)
                    putInt("roomTypePrice", roomType.price)
                    // THÊM: Truyền tiếp thông tin tìm kiếm
                    putLong("checkInDate", checkInDate)
                    putLong("checkOutDate", checkOutDate)
                    putInt("numberOfGuests", numberOfGuests)
                    putInt("numberOfRooms", numberOfRooms)
                }

                try {
                    findNavController().navigate(
                        com.example.btl.R.id.bookingFragment,
                        bundle
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Đặt phòng ${roomType.name} - ${roomType.price} VNĐ/đêm",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        binding.recyclerViewRoomTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roomTypeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.viewMapButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Xem bản đồ", Toast.LENGTH_SHORT).show()
        }

        binding.callButton?.setOnClickListener {
            val contact = viewModel.propertyDetail.value?.contact
            if (!contact.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Gọi: $contact", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.propertyDetail.collect { property ->
                        property?.let {
                            binding.hotelName.text = it.name
                            binding.hotelAddress.text = it.address
                            binding.hotelDescription.text = it.description
                            binding.checkInTime.text = "Nhận phòng: ${it.checkin}"
                            binding.checkOutTime.text = "Trả phòng: ${it.checkout}"

                            Glide.with(requireContext())
                                .load(it.image)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(binding.hotelImage)
                        }
                    }
                }

                launch {
                    viewModel.roomTypes.collect { roomTypes ->
                        roomTypeAdapter.submitList(roomTypes)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.error.collect { errorMessage ->
                        if (errorMessage.isNotEmpty()) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
