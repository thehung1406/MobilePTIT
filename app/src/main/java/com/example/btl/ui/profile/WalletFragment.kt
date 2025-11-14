package com.example.btl.ui.profile

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl.databinding.DialogDepositBinding
import com.example.btl.databinding.DialogWithdrawBinding
import com.example.btl.databinding.FragmentWalletBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private val transactions = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter(transactions)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionsRecyclerView.adapter = adapter

        updateTransactionHistory()

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.depositButton.setOnClickListener {
            showDepositDialog()
        }

        binding.withdrawButton.setOnClickListener {
            showWithdrawDialog()
        }
    }

    private fun showDepositDialog() {
        val dialogBinding = DialogDepositBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.depositButton.setOnClickListener {
            val amount = dialogBinding.amountEditText.text.toString()
            if (amount.isNotEmpty()) {
                val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())
                transactions.add(Transaction("+ $amount$", currentDate))
                updateTransactionHistory()
                adapter.notifyItemInserted(transactions.size - 1)

                Toast.makeText(requireContext(), "Nạp tiền thành công: $amount", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showWithdrawDialog() {
        val dialogBinding = DialogWithdrawBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.withdrawButton.setOnClickListener {
            val amount = dialogBinding.amountEditText.text.toString()
            if (amount.isNotEmpty()) {
                val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())
                transactions.add(Transaction("- $amount$", currentDate))
                updateTransactionHistory()
                adapter.notifyItemInserted(transactions.size - 1)

                Toast.makeText(requireContext(), "Rút tiền thành công: $amount", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateTransactionHistory() {
        if (transactions.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.transactionsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.transactionsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}