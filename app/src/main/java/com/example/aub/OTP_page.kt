package com.example.aub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aub.databinding.ActivityOtpPageBinding

class OTP_page : AppCompatActivity() {

    private lateinit var binding: ActivityOtpPageBinding
    private val correctOTP = "1111"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.submit.setOnClickListener {
            val enteredOtp = binding.OTP.text.toString().trim()

            if (enteredOtp == correctOTP) {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, St_Home::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
