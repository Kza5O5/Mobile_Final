package com.example.aub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.aub.databinding.ActivityUploadHwBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Upload_hw : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var edtName: EditText
    lateinit var edtId: EditText
    lateinit var edtDateTime: EditText
    lateinit var edtDescription: EditText
    lateinit var btnSubmit: Button
    lateinit var binding : ActivityUploadHwBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val storageRef = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadHwBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_upload_hw)

        imageView = findViewById(R.id.uploadimage)
        edtName = findViewById(R.id.edtname)
        edtId = findViewById(R.id.edtid)
        edtDateTime = findViewById(R.id.datetime)
        edtDescription = findViewById(R.id.des)
        btnSubmit = findViewById(R.id.btnsave)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
        btnSubmit.setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebase()
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebase() {
        val fileName = "uploads/${UUID.randomUUID()}.jpg"
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("ImageUpload", "Image URL: $imageUrl")
                    saveFormData(imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseStorage", "Error: ", it)
            }
    }

    private fun saveFormData(imageUrl: String) {
        val name = edtName.text.toString().trim()
        val id = edtId.text.toString().trim()
        val datetime = edtDateTime.text.toString().trim()
        val description = edtDescription.text.toString().trim()

        if (name.isEmpty() || id.isEmpty() || datetime.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "name" to name,
            "studentId" to id,
            "datetime" to datetime,
            "description" to description,
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("HomeworkUploads")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Submitted successfully!", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Data added with ID: ${it.id}")
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data save failed: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error: ", it)
            }

    }

    fun clearFields() {
        binding.uploadimage.setImageDrawable(null)
        binding.edtname.text.clear()
        binding.edtid.text.clear()
        binding.datetime.text.clear()
        binding.des.text.clear()

    }
}
