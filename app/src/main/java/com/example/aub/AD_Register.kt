package com.example.aub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aub.databinding.ActivityAdRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AD_Register : AppCompatActivity() {

    private lateinit var binding: ActivityAdRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnSubmit.setOnClickListener {
            val studentID = binding.etStudentId.text.toString().trim()
            val studentName = binding.etStudentName.text.toString().trim()
            val studentAge = binding.etStudentAge.text.toString().trim()
            val studentEmail = binding.etStudentEmail.text.toString().trim()
            val studentPassword = binding.etPassword.text.toString().trim()

            if (studentID.isEmpty() || studentName.isEmpty() || studentAge.isEmpty()
                || studentEmail.isEmpty() || studentPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(studentEmail, studentPassword)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {

                        val student = HelperClass(
                            studentID = studentID,
                            studentName = studentName,
                            studentAge = studentAge,
                            studentEmail = studentEmail
                        )

                        firestore.collection("Students").document(studentID)
                            .set(student)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Student registered successfully", Toast.LENGTH_SHORT).show()
                                clearFields()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun clearFields() {
        binding.etStudentId.text.clear()
        binding.etStudentName.text.clear()
        binding.etStudentAge.text.clear()
        binding.etStudentEmail.text.clear()
        binding.etPassword.text.clear()
    }
}
