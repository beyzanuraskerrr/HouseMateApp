package com.beyzasker.housemateapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beyzasker.housemateapp.model.UserModel



class ProfileAdapter(
    private val userList: ArrayList<UserModel>,
    private val onItemClickListener: (UserModel) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val educationTextView: TextView = itemView.findViewById(R.id.educationTextView)
        private val detailButton: Button = itemView.findViewById(R.id.detailButton)

        init {
            detailButton.setOnClickListener {
                val userModel = userList[adapterPosition]
                val intent = Intent(itemView.context, ProfileActivity::class.java)
                intent.putExtra("userModel", userModel)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(userModel: UserModel) {
            fullNameTextView.text = userModel.fullName
            emailTextView.text = userModel.email
            educationTextView.text = userModel.education

            // Fotoğrafı Base64'den Bitmap'e dönüştür
            val decodedBytes = Base64.decode(userModel.photo, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            profileImageView.setImageBitmap(decodedBitmap)

                onItemClickListener(userModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_summary, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val userModel = userList[position]
        holder.bind(userModel)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
