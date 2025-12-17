package com.example.btl.ui.profile

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btl.R
import com.example.btl.api.ApiClient
import com.example.btl.databinding.FragmentAccountSettingsBinding
import com.example.btl.model.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingsFragment : Fragment() {

    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.changePasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettingsFragment_to_changePasswordFragment)
        }
    }

    private fun loadUserProfile() {
        val sharedPref = activity?.getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE)
        val token = sharedPref?.getString("ACCESS_TOKEN", null)

        if (token == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            // Redirect to login or handle error
            return
        }

        ApiClient.authService.getProfile("Bearer $token").enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let {
                        binding.nameTextview.text = it.fullName
                        binding.emailTextview.text = it.email
                        binding.phoneTextview.text = it.phone
                    }
                } else {
                    Log.e("AccountSettings", "Failed to get profile: ${response.code()}")
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Log.e("AccountSettings", "Error loading profile", t)
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
