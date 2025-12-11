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
import com.example.btl.model.CreatePaymentRequest
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
        
        // Hide FAB as requested by user context (focus on displaying items)
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
                processPayment(booking)
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
                // Show loading? 
                
                val response = ApiClient.bookingService.getMyBookings("Bearer $token")
                allBookings = response
                
                // Filter based on current tab
                filterBookings(binding.tabLayout.selectedTabPosition)
                
            } catch (e: Exception) {
                Log.e("TripsFragment", "Error loading bookings", e)
                Toast.makeText(requireContext(), "Lỗi tải danh sách: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun filterBookings(tabPosition: Int) {
        val filteredList = when (tabPosition) {
            0 -> allBookings.filter { it.status?.uppercase() != "CANCELLED" && it.status?.uppercase() != "COMPLETED" }
            1 -> allBookings.filter { it.status?.uppercase() == "COMPLETED" } // Hoàn tất
            2 -> allBookings.filter { it.status?.uppercase() == "CANCELLED" } // Đã hủy
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
        val bookingId = booking.bookingId
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.bookingService.cancelBooking("Bearer $token", bookingId)
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                loadMyBookings()
            } catch (e: Exception) {
                // Dù có lỗi, vẫn reload lại danh sách booking vì có thể backend đã xử lý thành công rồi
                Log.e("TripsFragment", "Cancel failed but trying to reload list: ${e.message}")
                Toast.makeText(requireContext(), "Đang cập nhật lại trạng thái...", Toast.LENGTH_SHORT).show()
                loadMyBookings()
            }
        }
    }
    
    private fun processPayment(booking: BookingResponse) {
        val prefs = requireContext().getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", "") ?: ""
        val bookingId = booking.bookingId
        val amount = 1000000000 // Hardcoded amount as requested

        if (amount <= 0) {
            Toast.makeText(requireContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Step 1: Create Payment
                Toast.makeText(requireContext(), "Đang tạo yêu cầu thanh toán...", Toast.LENGTH_SHORT).show()
                val createPaymentRequest = CreatePaymentRequest(bookingId = bookingId, amount = amount)
                val createResponse = ApiClient.paymentService.createPayment("Bearer $token", createPaymentRequest)

                // Step 2: Confirm Payment
                Toast.makeText(requireContext(), "Đang xác nhận thanh toán...", Toast.LENGTH_SHORT).show()
                val confirmResponse = ApiClient.paymentService.confirmPayment("Bearer $token", createResponse.paymentId)
                
                // Success
                Toast.makeText(requireContext(), confirmResponse.message, Toast.LENGTH_LONG).show()

                // Reload list to update status
                loadMyBookings()

            } catch (e: Exception) {
                Log.e("TripsFragment", "Payment process failed", e)
                Toast.makeText(requireContext(), "Thanh toán thất bại: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
