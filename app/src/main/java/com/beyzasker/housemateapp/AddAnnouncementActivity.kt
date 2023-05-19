package com.beyzasker.housemateapp


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddAnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userDetails: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_announcement)

        db = Firebase.firestore
        auth = Firebase.auth

        val user = auth.currentUser

        db.collection("Users").whereEqualTo("uid", user!!.uid).get().addOnSuccessListener {
            if (!it.isEmpty) {
                userDetails = convertToUserModel(it)
            }
        }

        val editText = findViewById<EditText>(R.id.editTextAnnouncementDesc)

        val addButton = findViewById<Button>(R.id.addAnnouncementButton)
        addButton.setOnClickListener {
            val temp = hashMapOf(
                "uid" to userDetails.uid,
                "fullName" to userDetails.fullName,
                "description" to editText.text.toString(),
                "entryDateTime" to Timestamp.now()
            )

            db.collection("Announcements").add(temp).addOnSuccessListener { documentReference ->
                Toast.makeText(
                    baseContext,
                    "Successfully added!",
                    Toast.LENGTH_SHORT,
                ).show()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(
                    baseContext,
                    "Failed!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun convertToUserModel(snapshot: QuerySnapshot): UserModel {
        val userDetails = snapshot.documents[0]

        return UserModel(
            userDetails["uid"].toString(),
            userDetails["fullName"].toString(),
            userDetails["email"].toString(),
            userDetails["entryYear"].toString(),
            userDetails["gradYear"].toString(),
            userDetails["number"].toString(),
            userDetails["photo"].toString(),
            userDetails["education"].toString(),
            userDetails["state"].toString(),
            userDetails["time"].toString(),
            userDetails["distance"].toString(),
            userDetails["nameArr"] as List<String>,
            userDetails["isAdmin"] as Boolean
        )
    }
}