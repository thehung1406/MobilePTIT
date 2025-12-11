package com.example.btl.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.btl.R
import com.example.btl.databinding.FragmentMapBinding
import com.example.btl.model.Property
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel: MapViewModel by viewModels()

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var searchMarker: Marker? = null
    private var searchJob: Job? = null
    private lateinit var suggestionAdapter: SuggestionAdapter
    private var currentSelectedProperty: Property? = null
    private var roadOverlay: Polyline? = null

    private var initialLocationSet = false

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
        setupObservers()

        binding.fabMyLocation.setOnClickListener {
            if (::locationOverlay.isInitialized && locationOverlay.myLocation != null) {
                binding.map.controller.animateTo(locationOverlay.myLocation)
            }
        }

        binding.btnCloseInfo.setOnClickListener {
            binding.propertyInfoCard.visibility = View.GONE
            currentSelectedProperty = null
            roadOverlay?.let { binding.map.overlays.remove(it) }
            binding.map.invalidate()
        }

        binding.tvViewDetail.setOnClickListener {
            currentSelectedProperty?.let { property ->
                val bundle = Bundle().apply {
                    putInt("propertyId", property.id)
                    putString("propertyName", property.name)
                }
                findNavController().navigate(R.id.action_mapFragment_to_hotelDetailFragment, bundle)
            } ?: run {
                Toast.makeText(requireContext(), "Vui lòng chọn một khách sạn", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnDirections.setOnClickListener {
            drawRouteToProperty()
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
                        delay(300)
                        searchLocation(newText, 5)
                    }
                } else {
                    binding.suggestionsRecyclerView.visibility = View.GONE
                }
                return true
            }
        })

        val lat = arguments?.getDouble("latitude", 0.0) ?: 0.0
        val lon = arguments?.getDouble("longitude", 0.0) ?: 0.0
        val name = arguments?.getString("propertyName")

        if (lat != 0.0 && lon != 0.0) {
            initialLocationSet = true
            binding.map.post {
                val point = GeoPoint(lat, lon)
                binding.map.controller.setCenter(point)
                binding.map.controller.setZoom(18.0)
                addSingleMarker(point, name ?: "Địa điểm đã chọn")
            }
        }
    }

    private fun setupRecyclerView() {
        suggestionAdapter = SuggestionAdapter { address ->
            binding.searchView.setQuery(address.getAddressLine(0), true)
            binding.suggestionsRecyclerView.visibility = View.GONE
            searchLocation(address.getAddressLine(0), 1)
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

        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled = true
        binding.map.overlays.add(rotationGestureOverlay)

        binding.map.controller.setZoom(15.0)
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)

        locationOverlay.runOnFirstFix {
            if (!initialLocationSet) {
                activity?.runOnUiThread {
                    binding.map.controller.setCenter(locationOverlay.myLocation)
                    binding.map.controller.animateTo(locationOverlay.myLocation)
                }
            }
        }
    }

    private fun setupObservers() {
        mapViewModel.loadProperties()
        mapViewModel.properties.observe(viewLifecycleOwner) {
            drawPropertyMarkers(it)
        }
        mapViewModel.isLoading.observe(viewLifecycleOwner) { 
            // Handle loading state 
        }
        mapViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun drawPropertyMarkers(properties: List<Property>) {
        for (property in properties) {
            if (property.latitude != null && property.longitude != null) {
                val marker = Marker(binding.map)
                marker.position = GeoPoint(property.latitude, property.longitude)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = property.name
                marker.relatedObject = property

                marker.setOnMarkerClickListener { clickedMarker, _ ->
                    val clickedProperty = clickedMarker.relatedObject as? Property
                    clickedProperty?.let { showPropertyInfo(it) }
                    true
                }
                binding.map.overlays.add(marker)
            }
        }
        binding.map.invalidate()
    }
    
    private fun showPropertyInfo(property: Property) {
        currentSelectedProperty = property

        binding.propertyInfoCard.visibility = View.VISIBLE
        binding.tvPropertyName.text = property.name
        binding.tvPropertyAddress.text = property.address

        binding.btnDirections.isEnabled = true // Bật lại nút khi chọn ks mới
        
        if (!property.image.isNullOrEmpty()) {
            Glide.with(this)
                .load(property.image)
                .placeholder(R.drawable.ic_launcher_background) 
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgProperty)
        } else {
             binding.imgProperty.setImageResource(R.drawable.ic_launcher_background)
        }
        
        roadOverlay?.let { binding.map.overlays.remove(it) }
        binding.map.invalidate()
    }
    
    private fun addSingleMarker(geoPoint: GeoPoint, title: String) {
        searchMarker?.let {
            binding.map.overlays.remove(it)
        }

        searchMarker = Marker(binding.map)
        searchMarker?.position = geoPoint
        searchMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        searchMarker?.title = title
        binding.map.overlays.add(searchMarker)
        binding.map.invalidate()
    }

    private fun drawRouteToProperty() {
        val property = currentSelectedProperty
        val myLocation = locationOverlay.myLocation

        if (property?.latitude == null || property.longitude == null) {
            Toast.makeText(requireContext(), "Khách sạn không có thông tin vị trí.", Toast.LENGTH_SHORT).show()
            return
        }
        if (myLocation == null) {
            Toast.makeText(requireContext(), "Không tìm thấy vị trí của bạn. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnDirections.isEnabled = false // Vô hiệu hóa nút

        val startPoint = myLocation
        val endPoint = GeoPoint(property.latitude, property.longitude)
        
        val roadManager: RoadManager = GraphHopperRoadManager("2e85400a-44aa-4322-a886-f1b0c22e3c97", false)
        roadManager.addRequestOption("vehicle=car")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val road = roadManager.getRoad(arrayListOf(startPoint, endPoint))
                launch(Dispatchers.Main) {
                    // Không bật lại nút ở đây nữa
                    roadOverlay?.let { binding.map.overlays.remove(it) }
                    if (road.mStatus == Road.STATUS_OK) {
                        roadOverlay = RoadManager.buildRoadOverlay(road)
                        roadOverlay?.outlinePaint?.color = Color.BLUE
                        roadOverlay?.outlinePaint?.strokeWidth = 10f
                        binding.map.overlays.add(roadOverlay)
                        binding.map.invalidate()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi vẽ đường đi: " + road.mStatus, Toast.LENGTH_LONG).show()
                        binding.btnDirections.isEnabled = true // Bật lại nếu có lỗi
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                 launch(Dispatchers.Main) {
                     binding.btnDirections.isEnabled = true // Bật lại nếu có lỗi
                     Toast.makeText(requireContext(), "Không thể kết nối dịch vụ chỉ đường. Kiểm tra API Key và mạng.", Toast.LENGTH_LONG).show()
                 }
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

                            addSingleMarker(geoPoint, address.getAddressLine(0))
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
