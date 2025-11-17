package com.example.btl.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.btl.PromotionAdapter
import com.example.btl.R
import com.example.btl.databinding.FragmentHomeBinding
import com.example.btl.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupViewPager()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.hotelCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_hotelSearchFragment)
        }
        binding.mapCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
        }
        binding.comboCard.setOnClickListener {
            Toast.makeText(requireContext(), "Chức năng Combo tiết kiệm đang được phát triển", Toast.LENGTH_SHORT).show()
        }
        binding.activitiesCard.setOnClickListener {
            Toast.makeText(requireContext(), "Chức năng Hoạt động đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewPager() {
        mainViewModel.promotions.observe(viewLifecycleOwner) { promotions ->
            binding.promotionsViewpager.adapter = PromotionAdapter(promotions)

            scrollRunnable = object : Runnable {
                override fun run() {
                    binding.promotionsViewpager.currentItem = (binding.promotionsViewpager.currentItem + 1) % promotions.size
                    handler.postDelayed(this, 3000)
                }
            }
            handler.postDelayed(scrollRunnable, 3000)
        }
    }

    private fun observeViewModel() {
        mainViewModel.loadPromotions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(scrollRunnable)
        _binding = null
    }
}
