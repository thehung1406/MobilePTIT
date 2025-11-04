package com.example.btl.ui.deals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.adapter.VoucherAdapter
import com.example.btl.model.Voucher

class VoucherFragment : Fragment() {

    private lateinit var vouchersRecyclerView: RecyclerView
    private lateinit var emptyVouchersText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_voucher, container, false)
        vouchersRecyclerView = view.findViewById(R.id.vouchers_recycler_view)
        emptyVouchersText = view.findViewById(R.id.empty_vouchers_text)

        // --- TẠO DỮ LIỆU MẪU ---
        val sampleVouchers = listOf(
            Voucher(R.drawable.item_voucher_1, "Giảm 50% tiền khách sạn", "Chiết khấu Ưu đãi ứng dụng - Giảm đến 490,000đ. Chỉ 50 người đặt đầu tiên."),
            Voucher(R.drawable.item_voucher_2, "Giảm 25% tiền khách sạn", "Chiết khấu Ưu đãi ứng dụng - Giảm đến 295,000đ. Chỉ 75 người đặt đầu tiên.")
        )

        if (sampleVouchers.isEmpty()) {
            vouchersRecyclerView.visibility = View.GONE
            emptyVouchersText.visibility = View.VISIBLE
        } else {
            vouchersRecyclerView.visibility = View.VISIBLE
            emptyVouchersText.visibility = View.GONE
            
            // --- KẾT NỐI ADAPTER ---
            vouchersRecyclerView.layoutManager = LinearLayoutManager(context)
            vouchersRecyclerView.adapter = VoucherAdapter(sampleVouchers)
        }

        return view
    }
}