package com.example.btl.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.databinding.FragmentRefundBinding

class RefundFragment : Fragment() {

    private var _binding: FragmentRefundBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // TODO: Replace with actual data
        val refunds = emptyList<Refund>()

        if (refunds.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.refundsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.refundsRecyclerView.visibility = View.VISIBLE
            binding.refundsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.refundsRecyclerView.adapter = RefundAdapter(refunds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}