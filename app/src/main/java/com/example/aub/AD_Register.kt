package com.example.aub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aub.databinding.ActivityAdRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AD_Register : AppCompatActivity() {

    lateinit var binding: ActivityAdRegisterBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    val storageRef = FirebaseStorage.getInstance().reference

    val PICK_IMAGE_REQUEST = 100
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Open gallery
        binding.uploadimage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }

        binding.btnSubmit.setOnClickListener {
            val studentID = binding.etStudentId.text.toString().trim()
            val studentName = binding.etStudentName.text.toString().trim()
            val gender = binding.etGender.text.toString().trim()
            val dob = binding.etDOB.text.toString().trim()
            val email = binding.etStudentEmail.text.toString().trim().lowercase()
            val password = binding.etPassword.text.toString().trim()

            if (studentID.isEmpty() || studentName.isEmpty() || gender.isEmpty()
                || dob.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        uploadImageToFirebase(uid, studentID, studentName, gender, dob, email)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Auth failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            binding.uploadimage.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebase(
        uid: String,
        studentID: String,
        studentName: String,
        gender: String,
        dob: String,
        email: String
    ) {
        val fileName = "profile_images/${uid}_${UUID.randomUUID()}.jpg"
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveToFirestore(uid, studentID, studentName, gender, dob, email, imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToFirestore(
        uid: String,
        studentID: String,
        studentName: String,
        gender: String,
        dob: String,
        email: String,
        imageUrl: String
    ) {
        val data = hashMapOf(
            "studentID" to studentID,
            "studentName" to studentName,
            "gender" to gender,
            "dob" to dob,
            "email" to email,
            "imageUrl" to imageUrl
        )

        // Save inside: Students → [UID] → Students → [studentID]
        firestore.collection("Students").document(uid)
            .collection("Students").document(studentID)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Student Registered Successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.etStudentId.text.clear()
        binding.etStudentName.text.clear()
        binding.etGender.text.clear()
        binding.etDOB.text.clear()
        binding.etStudentEmail.text.clear()
        binding.etPassword.text.clear()
        binding.uploadimage.setImageResource(R.drawable.folder)
        imageUri = null
    }
}
