package com.example.foodies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodies.databinding.FragmentNotificationBinding
import com.example.foodies.databinding.ItemNotificationBinding

// Data class for notification
data class NotificationItem(
    val title: String,
    val description: String,
    val time: String,
    val day: String
) : java.io.Serializable

// Adapter for RecyclerView
class NotificationAdapter(
    private val notifications: List<NotificationItem>,
    private val onItemClick: (NotificationItem) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemNotificationBinding,
        private val onItemClick: (NotificationItem) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationItem) {
            binding.notificationTitle.text = notification.title
            binding.notificationDescription.text = notification.description
            binding.notificationTime.text = notification.time
            binding.notificationDay.text = notification.day
            binding.root.setOnClickListener { onItemClick(notification) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with 10 notifications
        val notifications = listOf(
            NotificationItem(
                "New Recipe Alert!",
                "Check out our latest recipe for a delicious pasta dish.",
                "10:30 AM",
                "Today"
            ),
            NotificationItem(
                "Weekly Menu Update",
                "New dishes added to this week's menu. Plan your meals now!",
                "9:15 AM",
                "Yesterday"
            ),
            NotificationItem(
                "Cooking Tip",
                "Learn how to perfectly sear steak with our new guide.",
                "3:45 PM",
                "2 Days Ago"
            ),
            NotificationItem(
                "Community Event",
                "Join our live cooking class this weekend!",
                "1:20 PM",
                "3 Days Ago"
            ),
            NotificationItem(
                "App Update",
                "We've added new features to enhance your cooking experience.",
                "8:00 AM",
                "4 Days Ago"
            ),
            NotificationItem(
                "Seasonal Recipe",
                "Try our new pumpkin spice dessert for the fall season!",
                "11:45 AM",
                "5 Days Ago"
            ),
            NotificationItem(
                "Chef Spotlight",
                "Meet Chef Maria and her signature dish in our latest blog.",
                "6:30 PM",
                "6 Days Ago"
            ),
            NotificationItem(
                "Meal Prep Guide",
                "Save time with our new meal prep tips for busy weeks.",
                "8:20 AM",
                "7 Days Ago"
            ),
            NotificationItem(
                "Recipe Contest",
                "Submit your best recipe for a chance to win a prize!",
                "2:50 PM",
                "8 Days Ago"
            ),
            NotificationItem(
                "New Feature",
                "Explore our grocery list tool to shop smarter.",
                "10:00 AM",
                "10 Days Ago"
            )
        )

        binding.notificationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NotificationAdapter(notifications) { notification ->
                val bundle = Bundle().apply {
                    putSerializable("notification", notification)
                }
                val detailFragment = NotificationDetailFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Back arrow click to navigate to HomeFragment
        binding.backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}