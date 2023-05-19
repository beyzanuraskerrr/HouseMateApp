package com.beyzasker.housemateapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beyzasker.housemateapp.model.AnnouncementModel

// on below line we are creating a course rv adapter class.
class AnnouncementAdapter(
    // on below line we are passing variables
    // as course list and context
    private val announcementList: ArrayList<AnnouncementModel>,
    private val context: Context
) : RecyclerView.Adapter<AnnouncementAdapter.CourseViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnnouncementAdapter.CourseViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.announcement_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnnouncementAdapter.CourseViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        holder.description.text = announcementList.get(position).description
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return announcementList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val description: TextView = itemView.findViewById(R.id.idAnnouncementItem)
    }
}