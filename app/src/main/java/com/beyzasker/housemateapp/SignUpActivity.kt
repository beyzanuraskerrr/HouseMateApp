package com.beyzasker.housemateapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.IOException


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userPhotoUri: Uri
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            createAccount()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    userPhotoUri = uri
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        val pickPhotoButton = findViewById<Button>(R.id.buttonPickPhoto)
        pickPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun createAccount() {
        // [START create_user_with_email]

        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val entryYear = findViewById<EditText>(R.id.editTextEntryYear).text.toString()
        val gradYear = findViewById<EditText>(R.id.editTextGradYear).text.toString()
        val fullName = findViewById<EditText>(R.id.editTextFullName).text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                Toast.makeText(
                    baseContext,
                    "Successful!",
                    Toast.LENGTH_SHORT,
                ).show()

                val userModel = UserModel(
                    user!!.uid,
                    fullName,
                    email,
                    entryYear,
                    gradYear,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    arrayListOf(),
                    false
                )

                try {
                    this.contentResolver.openFileDescriptor(userPhotoUri, "r").use { pfd ->
                        if (pfd != null) {
                            val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val b = baos.toByteArray()
                            val base64Photo = Base64.encodeToString(b, Base64.DEFAULT)
                            userModel.photo = base64Photo
                        }
                    }
                } catch (ex: IOException) {
                    println("Fotograf secilirken hata olustu!")
                }

                insertUserRecord(userModel)

                val signUpIntent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(signUpIntent)
                finish()

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    task.exception.toString(),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        // [END create_user_with_email]
    }

    private fun insertUserRecord(model: UserModel) {

        val nameArr = Array(model.fullName.length) { "" }
        nameArr[0] = model.fullName[0].toString()

        for (i in 1 until model.fullName.length) {
            println(model.fullName[i])
            nameArr[i] = nameArr[i - 1] + model.fullName[i].toString()
        }

        println("nameArr: $nameArr")

        val dbUser = hashMapOf(
            "uid" to model.uid,
            "fullName" to model.fullName,
            "email" to model.email,
            "entryYear" to model.entryYear,
            "gradYear" to model.gradYear,
            "number" to model.number,
            "photo" to model.photo,
            "education" to model.education,
            "state" to model.state,
            "distance" to model.distance,
            "time" to model.time,
            "nameArr" to nameArr.toList(),
            "isAdmin" to false
        )

        db.collection("Users").add(dbUser).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}