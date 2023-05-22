package com.beyzasker.housemateapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyzasker.housemateapp.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment(), ProfileAdapter.OnProfileItemClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var queryResult: ArrayList<UserModel> // Değişiklik burada
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        db = Firebase.firestore
        queryResult = ArrayList() // Değişiklik burada
        profileAdapter = ProfileAdapter(queryResult, this)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = profileAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchUsers(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return view
    }

    private fun searchUsers(query: String) {
        db.collection("Users")
            .whereArrayContains("nameArr", query)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = ArrayList<UserModel>()
                    for (document in task.result?.documents ?: emptyList()) {
                        val userModel = document.toObject(UserModel::class.java)
                        userModel?.let { userList.add(it) }
                    }
                    queryResult.clear()
                    queryResult.addAll(userList)
                    profileAdapter.notifyDataSetChanged()
                } else {
                    Log.d("SearchFragment", "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onItemClick(userModel: UserModel) {
        val intent = Intent(requireContext(), OtherProfileActivity::class.java)
        intent.putExtra("userID", userModel.uid)
        startActivity(intent)
    }
}
