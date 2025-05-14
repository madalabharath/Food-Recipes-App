package com.example.foodies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodies.databinding.FragmentFoodListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodListFragment : Fragment() {

    private var _binding: FragmentFoodListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt("categoryId")?.toString() ?: return

        // Back arrow click to navigate back
        binding.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Fetch Category Name from Firebase
        val categoriesRef = FirebaseDatabase.getInstance().getReference("Category")
        categoriesRef.orderByChild("Id").equalTo(categoryId.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val category = data.getValue(Category::class.java)
                    if (category != null && category.Name != null) {
                        binding.categoryTitleTextView.text = category.Name
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load category: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Foods RecyclerView
        val allFoods = mutableListOf<Food>()
        val displayedFoods = mutableListOf<Food>()
        val foodAdapter = FoodAdapter(displayedFoods) { food ->
            val bundle = Bundle().apply { putSerializable("food", food) }
            val recipeDetailFragment = RecipeDetailFragment().apply { arguments = bundle }
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, recipeDetailFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.foodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = foodAdapter
        }

        // SearchView for filtering foods
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                displayedFoods.clear()
                val searchText = newText?.trim()?.lowercase() ?: ""
                if (searchText.isEmpty()) {
                    displayedFoods.addAll(allFoods)
                } else {
                    displayedFoods.addAll(allFoods.filter {
                        it.Title?.lowercase()?.contains(searchText) == true
                    })
                }
                foodAdapter.notifyDataSetChanged()
                binding.emptyTextView.visibility = if (displayedFoods.isEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })

        // Fetch Foods from Firebase
        val database = FirebaseDatabase.getInstance().getReference("Foods")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allFoods.clear()
                displayedFoods.clear()
                for (data in snapshot.children) {
                    val food = data.getValue(Food::class.java)
                    if (food != null && food.CategoryId == categoryId) {
                        allFoods.add(food)
                    }
                }
                displayedFoods.addAll(allFoods)
                foodAdapter.notifyDataSetChanged()
                binding.emptyTextView.visibility = if (displayedFoods.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load foods: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.emptyTextView.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}