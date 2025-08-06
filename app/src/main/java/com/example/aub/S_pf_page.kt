package com.example.aub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aub.databinding.ActivitySpfPageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class S_pf_page : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivitySpfPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpfPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserEmail = currentUser?.email

        if (currentUserEmail == null) {
            binding.edtname.setText("Not logged in")
            binding.edtid.setText("N/A")
            return
        }

        db.collection("Students")
            .whereEqualTo("email", currentUserEmail)
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
            }
            .addOnFailureListener { e ->
                binding.edtname.setText("Error")
                Log.e("Firestore", "Error fetching profile", e)
            }

        findViewById<ImageView>(R.id.permission).setOnClickListener {
            startActivity(Intent(this, Permission::class.java))
            finish()
        }

        findViewById<Button>(R.id.logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login_page::class.java))
            finish()
        }
    }
}