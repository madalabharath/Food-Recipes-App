package com.example.foodies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteFoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        recyclerView = view.findViewById(R.id.favorites_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = FavoriteFoodAdapter()
        recyclerView.adapter = adapter
        loadFavorites()
        return view
    }

    private fun loadFavorites() {
        val database = FirebaseDatabase.getInstance().getReference("Favorites")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<FavoriteFood>()
                for (foodSnapshot in snapshot.children) {
                    Log.d("FavoriteFragment", "Snapshot: ${foodSnapshot.value}")
                    val food = foodSnapshot.getValue(FavoriteFood::class.java)
                    food?.let { favorites.add(it) }
                }
                if (favorites.isEmpty()) {
                    Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Found ${favorites.size} favorites", Toast.LENGTH_SHORT).show()
                }
                adapter.submitList(favorites)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoriteFragment", "Database error: ${error.message}")
            }
        })
    }
}