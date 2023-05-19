package com.beyzasker.housemateapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beyzasker.housemateapp.model.AnnouncementModel
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnnouncementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnnouncementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    //swipe layout
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var announcementRV: RecyclerView
    lateinit var announcementRVAdapter: AnnouncementAdapter
    lateinit var announcementList: ArrayList<AnnouncementModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = Firebase.auth
        db = Firebase.firestore

        setHasOptionsMenu(true);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val redirectButton = view.findViewById<Button>(R.id.redirectAddAnnouncementButton)
        redirectButton.setOnClickListener {
            val intent = Intent(context, AddAnnouncementActivity::class.java)
            startActivity(intent)
        }

        // on below line we are initializing our views with their ids.
        swipeRefreshLayout = view.findViewById(R.id.container)
        announcementRV = view.findViewById<RecyclerView>(R.id.idRVAnnouncements)

        // on below line we are initializing our list
        announcementList = ArrayList()

        // on below line we are initializing our adapter
        announcementRVAdapter = AnnouncementAdapter(announcementList, requireContext())

        // on below line we are setting adapter to our recycler view.
        announcementRV.adapter = announcementRVAdapter

        getAnnouncementData()

        announcementRVAdapter.notifyDataSetChanged()

        swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            swipeRefreshLayout.isRefreshing = false

            getAnnouncementData()

            // on below line we are notifying adapter
            // that data has changed in recycler view.
            announcementRVAdapter.notifyDataSetChanged()
        }

    }

    private fun convertToAnnouncementModel(snapshot: QueryDocumentSnapshot): AnnouncementModel {

        return AnnouncementModel(
            snapshot.get("uid").toString(),
            snapshot.get("fullName").toString(),
            Timestamp.now(),
            snapshot.get("description").toString()
        )
    }

    private fun getAnnouncementData() {
        db.collection("Announcements").orderBy("entryDateTime", Query.Direction.DESCENDING).get()
            .addOnCompleteListener {
                announcementList.clear()
                if (!it.result.isEmpty) {
                    for (i in it.result) {
                        var temp = convertToAnnouncementModel(i)
                        announcementList.add(temp)
                    }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnnouncementFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnnouncementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}