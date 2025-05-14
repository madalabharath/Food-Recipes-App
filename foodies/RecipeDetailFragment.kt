package com.example.foodies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.foodies.databinding.FragmentRecipeDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve Food from arguments
        val food = arguments?.getSerializable("food") as? Food
        if (food == null || food.Id == null) {
            Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // Back arrow click to navigate back
        binding.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Share button click
        binding.shareBtn.setOnClickListener {
            val shareText = "Check out this recipe: ${food.Title}\nVideo: ${food.VideoLink ?: "N/A"}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Recipe"))
        }

        // Favorite button click
        binding.favoriteBtn.setOnClickListener {
            toggleFavorite(food)
        }

        // Video link click
        binding.recipeVideoLink.setOnClickListener {
            food.VideoLink?.let { link ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(intent)
            }
        }

        // Display details
        binding.recipeTitle.text = food.Title ?: "Unknown"
        binding.recipeDescription.text = food.Description ?: "No description available"
        binding.recipeCalories.text = food.Calorie?.toString()?.plus(" kcal") ?: "N/A"
        binding.recipeTime.text = food.TimeValue?.toString()?.plus(" min") ?: "N/A"
        binding.recipeStars.text = food.Star?.toString() ?: "N/A"
        binding.recipeServings.text = food.Servings?.toString()?.plus(" Serves") ?: "N/A"
        binding.recipeFrozen.text = if (food.Frozen == true) "Frozen" else "Non-Freezable"
        binding.recipeIngredients.text = food.Ingredients?.joinToString("\n") { "• $it" } ?: "No ingredients available"
        binding.recipeVegNonveg.text = food.VegOrNonVeg ?: "N/A"
        binding.recipeCookingProcess.text = food.CookingProcess ?: "No cooking process available"

        // Set veg/non-veg icon
        binding.nonvegVegIcon.setImageResource(
            when (food.VegOrNonVeg) {
                "Veg" -> R.drawable.green_circle
                "Non-Veg" -> R.drawable.red_circle
                else -> R.drawable.green_circle
            }
        )
        Glide.with(binding.recipeImage.context)
            .load(food.ImagePath)
            .placeholder(R.drawable.image2)
            .error(R.drawable.image2)
            .into(binding.recipeImage)

        // Check favorite status
        checkFavoriteStatus(food)
    }

    private fun toggleFavorite(food: Food) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please sign in to favorite", Toast.LENGTH_SHORT).show()
            return
        }
        val foodId = food.Id.toString()
        val favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(user.uid).child(foodId)

        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Remove from favorites
                    favoritesRef.removeValue().addOnSuccessListener {
                        updateFavoriteButton(false)
                        Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Add to favorites
                    favoritesRef.setValue(food).addOnSuccessListener {
                        updateFavoriteButton(true)
                        Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add favorite", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkFavoriteStatus(food: Food) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val foodId = food.Id.toString()
        val favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(user.uid).child(foodId)

        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isFavorite = snapshot.exists()
                updateFavoriteButton(isFavorite)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error checking favorite: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteBtn.setImageResource(
            if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_outline
        )
        binding.favoriteBtn.contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}