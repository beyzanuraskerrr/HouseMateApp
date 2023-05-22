package com.beyzasker.housemateapp

import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
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

                if (passwordEditText.isVisible) {
                    passwordEditText.isVisible = false
                    passwordTextView.isVisible = false
                }

                val updatedUserDetails = UserModel(
                    userDetails.uid,
                    fullNameTextView.text.toString(),
                    emailTextView.text.toString(),
                    userDetails.entryYear,
                    userDetails.gradYear,
                    numberTextView.text.toString(),
                    userDetails.photo,
                    educationTextView.text.toString(),
                    stateTextView.text.toString(),
                    distanceTextView.text.toString(),
                    timeTextView.text.toString(),
                    userDetails.nameArr,
                    userDetails.isAdmin
                )

                userDB.document(userDocID).set(updatedUserDetails).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Profile Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    // Hata durumunda yapılacak işlemler
                }

            } else {
                editButton.text = "save"

                fullNameTextView.isEnabled = true
                educationTextView.isEnabled = true
                yearTextView.isEnabled = true
                stateTextView.isEnabled = true
                distanceTextView.isEnabled = true
                timeTextView.isEnabled = true
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

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun convertBase64ToImage(photoString: String): Bitmap {
        val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
