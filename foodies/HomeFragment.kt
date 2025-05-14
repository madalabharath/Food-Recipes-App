package com.example.foodies

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodies.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var currentBannerIndex = 0
    private val bannerScrollRunnable = object : Runnable {
        override fun run() {
            val bannerWidthPx = (408 * resources.displayMetrics.density).toInt()
            val totalBanners = 3
            currentBannerIndex = (currentBannerIndex + 1) % totalBanners
            binding.bannerScrollView.smoothScrollTo(currentBannerIndex * bannerWidthPx, 0)
            updateDotIndicator()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize dot indicator
        updateDotIndicator()

        // Start automatic banner scrolling
        startBannerScrolling()

        // Handle manual scrolling
        binding.bannerScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val bannerWidthPx = (408 * resources.displayMetrics.density).toInt()
            val newIndex = (scrollX + bannerWidthPx / 2) / bannerWidthPx
            if (newIndex != currentBannerIndex) {
                currentBannerIndex = newIndex.coerceIn(0, 2)
                updateDotIndicator()
            }
        }

        // Notification Icon
        binding.notificationIcon.setOnClickListener {
            val notificationFragment = NotificationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, notificationFragment)
                .addToBackStack(null)
                .commit()
        }

        // Logout Icon
        binding.logoutIcon.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // Best Foods RecyclerView
        val bestFoods = mutableListOf<BestFood>()
        val bestFoodAdapter = BestFoodAdapter(bestFoods)
        binding.bestFoodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bestFoodAdapter
        }

        val foodsRef = FirebaseDatabase.getInstance().getReference("Foods")
        foodsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bestFoods.clear()
                for (data in snapshot.children) {
                    val food = data.getValue(BestFood::class.java)
                    if (food != null && food.BestFood) {
                        bestFoods.add(food)
                    }
                }
                bestFoodAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load best foods: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Categories RecyclerView
        val categories = mutableListOf<Category>()
        val categoryAdapter = CategoryAdapter(categories) { category, view ->
            val bundle = Bundle().apply { putInt("categoryId", category.Id) }
            val foodListFragment = FoodListFragment().apply { arguments = bundle }
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, foodListFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        val categoriesRef = FirebaseDatabase.getInstance().getReference("Category")
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (data in snapshot.children) {
                    val category = data.getValue(Category::class.java)
                    if (category != null) {
                        categories.add(category)
                    }
                }
                categoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load categories: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun startBannerScrolling() {
        handler.postDelayed(bannerScrollRunnable, 3000)
    }

    private fun stopBannerScrolling() {
        handler.removeCallbacks(bannerScrollRunnable)
    }

    private fun updateDotIndicator() {
        binding.dot1.setBackgroundResource(
            if (currentBannerIndex == 0) R.drawable.dot_active else R.drawable.dot_inactive
        )
        binding.dot2.setBackgroundResource(
            if (currentBannerIndex == 1) R.drawable.dot_active else R.drawable.dot_inactive
        )
        binding.dot3.setBackgroundResource(
            if (currentBannerIndex == 2) R.drawable.dot_active else R.drawable.dot_inactive
        )
    }

    override fun onPause() {
        super.onPause()
        stopBannerScrolling()
    }

    override fun onResume() {
        super.onResume()
        startBannerScrolling()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopBannerScrolling()
        _binding = null
    }
}