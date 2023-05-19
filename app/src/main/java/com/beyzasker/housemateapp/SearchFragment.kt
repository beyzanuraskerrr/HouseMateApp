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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var queryResult: ArrayList<UserModel>
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        queryResult = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        profileAdapter = ProfileAdapter(queryResult) { userModel ->
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("userModel", userModel)
            startActivity(intent)
        }



        recyclerView.adapter = profileAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getAllUser(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getAllUser(text: String) {
        db.collection("Users").whereArrayContains("nameArr", text).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = ArrayList<UserModel>()
                    for (document in task.result) {
                        val userModel = document.toObject(UserModel::class.java)
                        userList.add(userModel)
                    }
                    queryResult.clear()
                    queryResult.addAll(userList)
                    profileAdapter.notifyDataSetChanged()
                } else {
                    Log.d("SearchFragment", "Error getting documents: ", task.exception)
                }
            }
    }

}


