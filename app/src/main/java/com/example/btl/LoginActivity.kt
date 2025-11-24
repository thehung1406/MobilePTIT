package com.example.btl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.btl.api.ApiClient
import com.example.btl.databinding.ActivityLoginBinding
import com.example.btl.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerTextview.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.emailEdittext.text.toString().trim()
        val password = binding.passwordEdittext.text.toString().trim()

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

        if (password.isEmpty()) {
            binding.passwordEdittext.error = "Mật khẩu không được để trống"
            binding.passwordEdittext.requestFocus()
            return
        }

        ApiClient.authService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (loginResponse.otpRequired) {
                            // TODO: Chuyển đến màn hình OTP
                            Toast.makeText(this@LoginActivity, "Yêu cầu xác thực OTP", Toast.LENGTH_SHORT).show()
                        } else {
                            // Lưu trữ token
                            saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                            Toast.makeText(this@LoginActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginActivity", "Đăng nhập thất bại: ${response.code()} - $errorBody")
                    Toast.makeText(this@LoginActivity, "Đăng nhập thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Lỗi mạng khi đăng nhập", t)
                Toast.makeText(this@LoginActivity, "Lỗi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        val sharedPref = getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }
}