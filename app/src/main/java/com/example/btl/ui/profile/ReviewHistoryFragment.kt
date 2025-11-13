package com.example.btl.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.databinding.FragmentReviewHistoryBinding

class ReviewHistoryFragment : Fragment() {

    private var _binding: FragmentReviewHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // TODO: Replace with actual data
        val reviews = emptyList<Review>()

        if (reviews.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.reviewsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.reviewsRecyclerView.visibility = View.VISIBLE
            binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.reviewsRecyclerView.adapter = ReviewHistoryAdapter(reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}