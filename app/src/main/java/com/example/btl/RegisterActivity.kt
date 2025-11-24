package com.example.btl

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.btl.api.ApiClient
import com.example.btl.databinding.ActivityRegisterBinding
import com.example.btl.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dobEdittext.setOnClickListener {
            showDatePickerDialog()
        }

        binding.registerButton.setOnClickListener {
            validateData()
        }

        binding.loginTextview.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                binding.dobEdittext.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun validateData() {
        val fullName = binding.fullnameEdittext.text.toString().trim()
        val email = binding.emailEdittext.text.toString().trim()
        val phone = binding.phoneEdittext.text.toString().trim()
        val dob = binding.dobEdittext.text.toString().trim()
        val password = binding.passwordEdittext.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEdittext.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.fullnameEdittext.error = "Họ và tên không được để trống"
            binding.fullnameEdittext.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.emailEdittext.error = "Email không được để trống"
            binding.emailEdittext.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEdittext.error = "Email không hợp lệ"
            binding.emailEdittext.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            binding.phoneEdittext.error = "Số điện thoại không được để trống"
            binding.phoneEdittext.requestFocus()
            return
        }

        if (dob.isEmpty()) {
            binding.dobEdittext.error = "Năm sinh không được để trống"
            binding.dobEdittext.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.passwordEdittext.error = "Mật khẩu không được để trống"
            binding.passwordEdittext.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.passwordEdittext.error = "Mật khẩu phải có ít nhất 6 ký tự"
            binding.passwordEdittext.requestFocus()
            return
        }

        if (confirmPassword != password) {
            binding.confirmPasswordEdittext.error = "Mật khẩu không khớp"
            binding.confirmPasswordEdittext.requestFocus()
            return
        }

        if (!binding.termsCheckbox.isChecked) {
            binding.termsCheckbox.error = "Bạn phải đồng ý với các điều khoản"
            binding.termsCheckbox.requestFocus()
            return
        }

        val registerRequest = RegisterRequest(email, fullName, phone, password)
        ApiClient.authService.register(registerRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterActivity", "Đăng ký thất bại: ${response.code()} - $errorBody")
                    Toast.makeText(this@RegisterActivity, "Đăng ký thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterActivity", "Lỗi mạng khi đăng ký", t)
                Toast.makeText(this@RegisterActivity, "Lỗi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}