package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.btl.databinding.ActivityLoginBinding

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
        val username = binding.usernameEdittext.text.toString().trim()
        val password = binding.passwordEdittext.text.toString().trim()

        if (username.isEmpty()) {
            binding.usernameEdittext.error = "Tên đăng nhập không được để trống"
            binding.usernameEdittext.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.passwordEdittext.error = "Mật khẩu không được để trống"
            binding.passwordEdittext.requestFocus()
            return
        }

        // TODO: Xử lý đăng nhập
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}