package com.example.aub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aub.databinding.ActivityResetPageBinding
import com.google.firebase.auth.FirebaseAuth

class Reset_Page : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var emailEditText: EditText
    lateinit var resetButton: Button
    lateinit var binding: ActivityResetPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
//        setContentView(R.layout.activity_reset_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.txtreset)
        resetButton = findViewById(R.id.btnreset)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }

            }
        }
    }
}