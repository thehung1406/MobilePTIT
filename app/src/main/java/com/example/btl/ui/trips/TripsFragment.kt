package com.example.btl.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout

class TripsFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var emptyView: TextView
    private lateinit var hotelsRecyclerView: RecyclerView
    private lateinit var fabAddBooking: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips, container, false)
        tabLayout = view.findViewById(R.id.tab_layout)
        emptyView = view.findViewById(R.id.empty_view)
        hotelsRecyclerView = view.findViewById(R.id.hotels_recycler_view)
        fabAddBooking = view.findViewById(R.id.fab_add_booking)

        fabAddBooking.setOnClickListener {
            findNavController().navigate(R.id.action_tripsFragment_to_hotelSearchFragment)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        fabAddBooking.visibility = View.VISIBLE
                        hotelsRecyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                        emptyView.text = "Quý khách không có đặt chỗ sắp tới nào"
                    }
                    1 -> {
                        fabAddBooking.visibility = View.GONE
                        hotelsRecyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                        emptyView.text = "Quý khách không có đặt chỗ đã hoàn thành gần đây"
                    }
                    2 -> {
                        fabAddBooking.visibility = View.GONE
                        hotelsRecyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                        emptyView.text = "Quý khách không có đặt chỗ đã hủy gần đây"
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Set initial state for the first tab
        fabAddBooking.visibility = View.VISIBLE

        return view
    }
}
