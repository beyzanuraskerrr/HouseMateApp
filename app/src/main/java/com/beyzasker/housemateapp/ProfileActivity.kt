package com.beyzasker.housemateapp

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userDetails: UserModel
    private var buttonClickCount = 0
    private lateinit var userDocID: String
    private val editableFactory: Editable.Factory = Editable.Factory.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = Firebase.firestore
        auth = Firebase.auth

        val user = auth.currentUser
        val userDB = db.collection("Users")

        val fullNameTextView = findViewById<EditText>(R.id.ProfileActivityFullName)
        val educationTextView = findViewById<EditText>(R.id.ProfileActivityEducation)
        val yearTextView = findViewById<EditText>(R.id.ProfileActivityYear)
        val emailTextView = findViewById<EditText>(R.id.ProfileActivityEmail)
        val stateTextView = findViewById<EditText>(R.id.ProfileActivityState)
        val distanceTextView = findViewById<EditText>(R.id.ProfileActivityDistance)
        val timeTextView = findViewById<EditText>(R.id.ProfileActivityTime)
        val numberTextView = findViewById<EditText>(R.id.ProfileActivityNumber)
        val photoImageView = findViewById<ImageView>(R.id.ProfileActivityPhoto)
        val passwordEditText = findViewById<EditText>(R.id.ProfileActivityPassword)
        val passwordTextView = findViewById<TextView>(R.id.ProfileActivityPw)

        passwordEditText.isVisible = false
        passwordTextView.isVisible = false

        val userDocRef = userDB.whereEqualTo("uid", user!!.uid)

        userDocRef.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                userDetails = documentSnapshot.toObject(UserModel::class.java)!!
                userDocID = documentSnapshot.id

                fullNameTextView.text = editableFactory.newEditable(userDetails.fullName)
                educationTextView.text = editableFactory.newEditable(userDetails.education)
                yearTextView.text =
                    editableFactory.newEditable(userDetails.entryYear + "-" + userDetails.gradYear)
                stateTextView.text = editableFactory.newEditable(userDetails.state)
                distanceTextView.text = editableFactory.newEditable(userDetails.distance)
                timeTextView.text = editableFactory.newEditable(userDetails.time)
                emailTextView.text = editableFactory.newEditable(userDetails.email)
                numberTextView.text = editableFactory.newEditable(userDetails.number)
                photoImageView.setImageBitmap(convertBase64ToImage(userDetails.photo))
            }
        }.addOnFailureListener {
            // Hata durumunda yapılacak işlemler
        }

        val editButton = findViewById<Button>(R.id.editProfileButton)

        editButton.setOnClickListener {
            if (++buttonClickCount % 2 == 0) {
                editButton.text = "edit"

                fullNameTextView.isEnabled = false
                educationTextView.isEnabled = false
                yearTextView.isEnabled = false
                stateTextView.isEnabled = false
                distanceTextView.isEnabled = false
                timeTextView.isEnabled = false
                emailTextView.isEnabled = false
                numberTextView.isEnabled = false

                if (passwordEditText.text.isNotEmpty()) {
                    user?.updatePassword(passwordEditText.text.toString())
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Password was updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Update password failed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }?.addOnFailureListener {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Update password failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                passwordEditText.isVisible = false
                passwordEditText.text.clear()
                passwordTextView.isVisible = false

                userDB.document(userDocID).update(
                    "fullName", fullNameTextView.text.toString(),
                    "education", educationTextView.text.toString(),
                    "entryYear", yearTextView.text.trim().substring(0, yearTextView.text.indexOf("-")),
                    "gradYear", yearTextView.text.trim().substring(yearTextView.text.indexOf("-") + 1),
                    "state", stateTextView.text.toString(),
                    "time", timeTextView.text.toString(),
                    "distance", distanceTextView.text.toString(),
                    "email", emailTextView.text.toString(),
                    "number", numberTextView.text.toString()
                ).addOnSuccessListener {
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@ProfileActivity, "Profile update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                editButton.text = "save"
                fullNameTextView.isEnabled = true
                educationTextView.isEnabled = true
                yearTextView.isEnabled = true
                stateTextView.isEnabled = true
                timeTextView.isEnabled = true
                distanceTextView.isEnabled = true
                emailTextView.isEnabled = true
                numberTextView.isEnabled = true
                passwordEditText.isVisible = true
                passwordTextView.isVisible = true
            }
        }

        photoImageView.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_photo, null)
            dialogBuilder.setView(dialogView)

            val dialogPhotoImageView = dialogView.findViewById<ImageView>(R.id.dialogPhotoImageView)
            dialogPhotoImageView.setImageBitmap(convertBase64ToImage(userDetails.photo))

            val dialog = dialogBuilder.create()
            dialog.show()
        }

        val userModel = intent.getParcelableExtra<UserModel>("userModel")
        if (userModel != null) {
            userDetails = userModel

            fullNameTextView.text = editableFactory.newEditable(userDetails.fullName)
            educationTextView.text = editableFactory.newEditable(userDetails.education)
            yearTextView.text =
                editableFactory.newEditable(userDetails.entryYear + "-" + userDetails.gradYear)
            stateTextView.text = editableFactory.newEditable(userDetails.state)
            distanceTextView.text = editableFactory.newEditable(userDetails.distance)
            timeTextView.text = editableFactory.newEditable(userDetails.time)
            emailTextView.text = editableFactory.newEditable(userDetails.email)
            numberTextView.text = editableFactory.newEditable(userDetails.number)
            photoImageView.setImageBitmap(convertBase64ToImage(userDetails.photo))
        }
    }

    private fun convertBase64ToImage(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
