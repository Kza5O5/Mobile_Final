package com.example.aub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.aub.databinding.ActivityStHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class St_Home : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityStHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewBinding
        binding = ActivityStHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email

        if (currentUserEmail == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("DEBUG", "Looking for student with email: $currentUserEmail")

        // Query Firestore for student
        db.collection("Students")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No student found for $currentUserEmail", Toast.LENGTH_SHORT).show()
                    Log.w("FIRESTORE", "No student document found.")
                } else {
                    val document = result.documents.first()
                    val studentName = document.getString("studentName") ?: "No name"
                    val studentID = document.getString("studentID") ?: "No ID"

                    Log.d("FIRESTORE", "Student found: $studentName / $studentID")

                    // Display values in EditText
                    binding.edtname.setText(studentName)
                    binding.edtid.setText(studentID)

                    // ✅ Load profile image if available
                    val imageUrl = document.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.pfp) // Optional: your default image
                            .error(R.drawable.pfp)       // Optional: shown on fail
                            .into(binding.gotopfp)           // ImageView in your layout
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting student info", Toast.LENGTH_SHORT).show()
                Log.e("FIRESTORE", "Fetch failed", e)
            }

        // Navigation buttons
        findViewById<ImageView>(R.id.gotopfp).setOnClickListener {
            startActivity(Intent(this, S_pf_page::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.gotopermission).setOnClickListener {
            startActivity(Intent(this, Permission::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.gotohomework).setOnClickListener {
            startActivity(Intent(this, Upload_hw::class.java))
            finish()
        }
    }
}
