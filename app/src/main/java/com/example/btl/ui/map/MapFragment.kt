package com.example.btl.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.databinding.FragmentMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var searchMarker: Marker? = null
    private var searchJob: Job? = null
    private lateinit var suggestionAdapter: SuggestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionsIfNecessary(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ))

        setupRecyclerView()

        binding.fabMyLocation.setOnClickListener {
            if (::locationOverlay.isInitialized && locationOverlay.myLocation != null) {
                binding.map.controller.animateTo(locationOverlay.myLocation)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob?.cancel()
                binding.suggestionsRecyclerView.visibility = View.GONE
                if (!query.isNullOrBlank()) {
                    searchLocation(query, 1)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                if (!newText.isNullOrBlank()) {
                    searchJob = lifecycleScope.launch {
                        delay(300) // Debounce
                        searchLocation(newText, 5) // Get up to 5 suggestions
                    }
                } else {
                    binding.suggestionsRecyclerView.visibility = View.GONE
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        suggestionAdapter = SuggestionAdapter { address ->
            binding.searchView.setQuery(address.getAddressLine(0), true)
            binding.suggestionsRecyclerView.visibility = View.GONE
        }
        binding.suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionAdapter
        }
    }

    private fun setupMap() {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().userAgentValue = "com.example.btl"
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        // Enable rotation gesture
        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled = true
        binding.map.overlays.add(rotationGestureOverlay)

        binding.map.controller.setZoom(15.0)
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)

        locationOverlay.runOnFirstFix {
            activity?.runOnUiThread {
                binding.map.controller.setCenter(locationOverlay.myLocation)
                binding.map.controller.animateTo(locationOverlay.myLocation)
            }
        }
    }

    private fun searchLocation(query: String, maxResults: Int) {
        if (!Geocoder.isPresent()) {
            Toast.makeText(requireContext(), "Dịch vụ Geocoder không khả dụng.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext())
            try {
                val addresses: List<Address> = geocoder.getFromLocationName(query, maxResults) ?: emptyList()
                launch(Dispatchers.Main) {
                    if (maxResults == 1) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val geoPoint = GeoPoint(address.latitude, address.longitude)

                            // Remove previous marker
                            searchMarker?.let {
                                binding.map.overlays.remove(it)
                            }

                            // Add new marker
                            searchMarker = Marker(binding.map)
                            searchMarker?.position = geoPoint
                            searchMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            searchMarker?.title = address.getAddressLine(0)
                            binding.map.overlays.add(searchMarker)

                            // Animate to the new location
                            binding.map.controller.animateTo(geoPoint)
                        } else {
                            Toast.makeText(requireContext(), "Không tìm thấy địa điểm.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        suggestionAdapter.updateSuggestions(addresses)
                        binding.suggestionsRecyclerView.visibility = if (addresses.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi khi tìm kiếm địa điểm.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS_REQUEST_CODE)
        } else {
            setupMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupMap()
            } else {
                Toast.makeText(requireContext(), "Quyền truy cập vị trí là cần thiết cho tính năng bản đồ.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
