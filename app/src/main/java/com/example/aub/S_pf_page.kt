package com.example.aub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.aub.databinding.ActivitySpfPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class S_pf_page : AppCompatActivity() {

    private lateinit var binding: ActivitySpfPageBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpfPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            binding.edtname.setText("Not logged in")
            binding.edtid.setText("N/A")
            return
        }

        val uid = currentUser.uid

        // Load student data
        db.collection("Students").document(uid).collection("Students")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    binding.edtname.setText("No data")
                    binding.edtid.setText("No data")
                    return@addOnSuccessListener
                }

                val document = result.documents.first()
                val student = document.toObject(HelperClass::class.java)

                binding.edtname.setText(student?.studentName ?: "N/A")
                binding.edtid.setText(student?.studentID ?: "N/A")
                binding.edtgender.setText(student?.gender ?: "N/A")
                binding.etDOB.setText(student?.dob ?: "N/A")

                student?.imageUrl?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.pfp)
                        .error(R.drawable.pfp)
                        .into(binding.pfp)  // Using viewBinding
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching profile", e)
                binding.edtname.setText("Error loading data")
            }

        // Permission navigation
        binding.permission.setOnClickListener {
            startActivity(Intent(this, Permission::class.java))
            finish()
        }

        // Logout button
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login_page::class.java))
            finish()
        }
    }
}
