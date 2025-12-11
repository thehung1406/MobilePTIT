package com.example.btl.ui.trips

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.api.ApiClient
import com.example.btl.databinding.FragmentTripsBinding
import com.example.btl.model.BookingResponse
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var tripsAdapter: TripsAdapter
    private var allBookings = listOf<BookingResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        
        // Hide FAB as requested by user context
        binding.fabAddBooking.visibility = View.GONE
        
        loadMyBookings()
    }
    
    private fun setupRecyclerView() {
        tripsAdapter = TripsAdapter(
            bookings = emptyList(),
            onCancelClick = { booking ->
                showCancelConfirmation(booking)
            },
            onPayClick = { booking ->
                Toast.makeText(requireContext(), "Chức năng thanh toán đang phát triển", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.hotelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tripsAdapter
        }
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterBookings(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun loadMyBookings() {
        val prefs = requireContext().getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", "") ?: ""
        
        if (token.isEmpty()) {
            binding.emptyView.text = "Vui lòng đăng nhập để xem đơn đặt phòng"
            binding.emptyView.visibility = View.VISIBLE
            binding.hotelsRecyclerView.visibility = View.GONE
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.bookingService.getMyBookings("Bearer $token")
                allBookings = response
                
                // Debug log
                Log.d("TripsFragment", "Loaded ${allBookings.size} bookings")
                allBookings.forEach { 
                    Log.d("TripsFragment", "Booking: ID=${it.bookingId}, Status=${it.status}") 
                }
                
                // Filter based on current tab
                filterBookings(binding.tabLayout.selectedTabPosition)
                
            } catch (e: Exception) {
                Log.e("TripsFragment", "Error loading bookings", e)
                Toast.makeText(requireContext(), "Lỗi tải danh sách: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun filterBookings(tabPosition: Int) {
        // Chuẩn hóa status để so sánh không phân biệt hoa thường
        val filteredList = when (tabPosition) {
            0 -> allBookings.filter { 
                val status = it.status?.uppercase() ?: ""
                status != "CANCELLED" && status != "COMPLETED" && status != "REJECTED"
                // Hiển thị tất cả ngoại trừ Cancelled/Completed/Rejected -> Bao gồm PENDING, CONFIRMED, NULL, v.v.
            }
            1 -> allBookings.filter { 
                val status = it.status?.uppercase() ?: ""
                status == "COMPLETED" 
            } 
            2 -> allBookings.filter { 
                val status = it.status?.uppercase() ?: ""
                status == "CANCELLED" || status == "REJECTED"
            } 
            else -> allBookings
        }
        
        tripsAdapter.updateData(filteredList)
        
        if (filteredList.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.hotelsRecyclerView.visibility = View.GONE
            
            val message = when (tabPosition) {
                0 -> "Quý khách không có đặt chỗ sắp tới nào"
                1 -> "Quý khách chưa hoàn tất chuyến đi nào"
                2 -> "Quý khách chưa hủy chuyến đi nào"
                else -> "Danh sách trống"
            }
            binding.emptyView.text = message
        } else {
            binding.emptyView.visibility = View.GONE
            binding.hotelsRecyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun showCancelConfirmation(booking: BookingResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hủy đặt phòng")
            .setMessage("Bạn có chắc muốn hủy đặt phòng #${booking.bookingId} không?")
            .setPositiveButton("Đồng ý") { _, _ ->
                cancelBooking(booking)
            }
            .setNegativeButton("Không", null)
            .show()
    }
    
    private fun cancelBooking(booking: BookingResponse) {
        val prefs = requireContext().getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", "") ?: ""
        val bookingId = booking.bookingId ?: return
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.bookingService.cancelBooking("Bearer $token", bookingId)
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                loadMyBookings()
            } catch (e: Exception) {
                Log.e("TripsFragment", "Cancel failed but trying to reload list: ${e.message}")
                // Vẫn reload lại danh sách booking vì có thể backend đã xử lý thành công rồi
                loadMyBookings()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
