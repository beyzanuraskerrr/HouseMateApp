package com.beyzasker.housemateapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtherProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userDetails: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile)

        db = Firebase.firestore

        val userID = intent?.getStringExtra("userID") ?: ""

        val fullNameTextView = findViewById<TextView>(R.id.OtherProfileActivityFullName)
        val educationTextView = findViewById<TextView>(R.id.OtherProfileActivityEducation)
        val yearTextView = findViewById<TextView>(R.id.OtherProfileActivityYear)
        val emailTextView = findViewById<TextView>(R.id.OtherProfileActivityEmail)
        val stateTextView = findViewById<TextView>(R.id.OtherProfileActivityState)
        val distanceTextView = findViewById<TextView>(R.id.OtherProfileActivityDistance)
        val timeTextView = findViewById<TextView>(R.id.OtherProfileActivityTime)
        val numberTextView = findViewById<TextView>(R.id.OtherProfileActivityNumber)
        val photoImageView = findViewById<ImageView>(R.id.OtherProfileActivityPhoto)

        val userDB = db.collection("Users")
        val userDocRef = userDB.document(userID)

        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                userDetails = documentSnapshot.toObject(UserModel::class.java)!!

                fullNameTextView.text = userDetails.fullName
                educationTextView.text = userDetails.education
                yearTextView.text = "${userDetails.entryYear}-${userDetails.gradYear}"
                stateTextView.text = userDetails.state
                distanceTextView.text = userDetails.distance.toString()
                timeTextView.text = userDetails.time
                emailTextView.text = userDetails.email
                numberTextView.text = userDetails.number
                if (userDetails.photo.isNotEmpty()) {
                    photoImageView.setImageBitmap(convertBase64ToImage(userDetails.photo))
                }
            }
        }.addOnFailureListener {
            // Hata durumunda yapılacak işlemler
        }
    }

    private fun convertBase64ToImage(photoString: String): Bitmap {
        val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
