package com.example.btl.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btl.LoginActivity
import com.example.btl.R
import com.example.btl.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        binding.logoutButton.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }

        binding.walletButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_walletFragment)
        }

        binding.refundButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_refundFragment)
        }

        binding.dealsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_reviewHistoryFragment)
        }

        binding.messagesButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_chatbotFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.profileImage.setImageURI(imageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
