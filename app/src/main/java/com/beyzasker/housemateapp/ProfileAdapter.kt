package com.beyzasker.housemateapp
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beyzasker.housemateapp.model.UserModel

class ProfileAdapter(
    private val userList: ArrayList<UserModel>,
    private val onProfileItemClickListener: OnProfileItemClickListener
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    interface OnProfileItemClickListener {
        fun onItemClick(userModel: UserModel)
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val educationTextView: TextView = itemView.findViewById(R.id.educationTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedUser = userList[position]
                    onProfileItemClickListener.onItemClick(clickedUser)
                }
            }
        }

        fun bind(userModel: UserModel) {
            fullNameTextView.text = userModel.fullName
            emailTextView.text = userModel.email
            educationTextView.text = userModel.education

            val decodedBytes = Base64.decode(userModel.photo, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            profileImageView.setImageBitmap(decodedBitmap)
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
