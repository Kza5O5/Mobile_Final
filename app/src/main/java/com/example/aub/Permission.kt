package com.example.aub

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aub.databinding.ActivityPermissionBinding
import com.google.firebase.firestore.FirebaseFirestore

class Permission : AppCompatActivity() {

    lateinit var binding: ActivityPermissionBinding
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.btnsubmit.setOnClickListener {
            val studentID = binding.edtid.text.toString().trim()
            val studentName = binding.edtname.text.toString().trim()
            val gender = getSelectedGender()
            val phoneNumber = binding.edtphonenumber.text.toString().trim()
            val major = binding.edtmajor.text.toString().trim()
            val session = binding.edtsession.text.toString().trim()
            val reason = binding.edtreason.text.toString().trim()

            if (studentID.isEmpty() || studentName.isEmpty() || gender.isEmpty()
                || phoneNumber.isEmpty() || major.isEmpty() || session.isEmpty() || reason.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Step 1: Create document with auto ID
            val ref = firestore.collection("Students")
                .document(studentName)
                .collection("st_permission")
                .document() // this creates the auto-ID but doesn't save yet

            val requestId = ref.id // get the auto-generated ID

            // Step 2: Create data object using your helper class
            val permissionData = PermissionData(
                request_id = requestId,
                studentID = studentID,
                studentName = studentName,
                gender = gender,
                phoneNumber = phoneNumber,
                major = major,
                session = session,
                reason = reason
            )

            // Step 3: Save it with that same ID
            ref.set(permissionData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Permission submitted with ID: $requestId", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getSelectedGender(): String {
        return when (binding.linear1.findViewById<android.widget.RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
            R.id.male -> "Male"
            R.id.female -> "Female"
            else -> ""
        }
    }

    private fun clearFields() {
        binding.edtname.text.clear()
        binding.edtid.text.clear()
        binding.edtphonenumber.text.clear()
        binding.edtmajor.text.clear()
        binding.edtsession.text.clear()
        binding.edtreason.text.clear()
        binding.linear1.findViewById<android.widget.RadioGroup>(R.id.radioGroup).clearCheck()
    }
}
