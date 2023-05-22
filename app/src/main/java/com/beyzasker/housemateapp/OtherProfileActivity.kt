package com.beyzasker.housemateapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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


        // Eşleşme butonunu tanımlayın ve tıklama olayını dinleyin
        val matchButton = findViewById<Button>(R.id.matchButton)
        matchButton.setOnClickListener {
            // Eşleşme talebi gönderilecek hedef kullanıcının UID'si
            val targetUserUid = "Hedef_Kullanıcı_UID"

            // Eşleşme talebini temsil eden bir belge oluşturun
            val matchRequestData = hashMapOf(
                "senderUid" to FirebaseAuth.getInstance().currentUser?.uid, // Talebi gönderen kullanıcının UID'si
                "timestamp" to FieldValue.serverTimestamp() // Talebin gönderildiği zaman
            )

            // Eşleşme talebini Firestore veritabanına ekleyin
            db.collection("MatchRequests")
                .document(targetUserUid)
                .set(matchRequestData)
                .addOnSuccessListener {
                    // Talep başarıyla gönderildiğinde yapılacak işlemler
                    Toast.makeText(this, "Eşleşme talebi gönderildi.", Toast.LENGTH_SHORT).show()

                    // Eşleşme talebi gönderildiğinde kullanıcıya bildirim gösterme işlemleri
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Eşleşme Talebi")
                        .setMessage("Bir eşleşme talebi aldınız!")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val dialog = builder.create()
                    dialog.show()
                }
                .addOnFailureListener { e ->
                    // Talep gönderme hatası durumunda yapılacak işlemler
                    Toast.makeText(this, "Eşleşme talebi gönderme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun convertBase64ToImage(photoString: String): Bitmap {
        val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
