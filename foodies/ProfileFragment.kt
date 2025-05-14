package com.example.foodies

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.foodies.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding not initialized")
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Load user profile
        loadUserProfile()

        // Edit Profile
        binding.editProfileButton.setOnClickListener { showEditProfileDialog() }

        // Your Favorites
        binding.favoritesOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, FavoriteFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Favorites not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Favorites navigation failed: ${e.message}")
            }
        }

        // Languages
        binding.languagesOption.setOnClickListener { showLanguageDialog() }


        // Security
        binding.securityOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, SecurityFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Security not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Security navigation failed: ${e.message}")
            }
        }

        // Notifications
        binding.notificationsOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, NotificationFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Notifications not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Notifications navigation failed: ${e.message}")
            }
        }

        // Help & Support
        binding.helpSupportOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, HelpSupportFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Help & Support not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "HelpSupport navigation failed: ${e.message}")
            }
        }

        // Terms & Policies
        binding.termsPoliciesOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, TermsPoliciesFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Terms & Policies not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "TermsPolicies navigation failed: ${e.message}")
            }
        }

        // Report a Problem
        binding.reportProblemOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, ReportProblemFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Report a Problem not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "ReportProblem navigation failed: ${e.message}")
            }
        }

        // FAQ
        binding.faqOption.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, FAQFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "FAQ not available", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "FAQ navigation failed: ${e.message}")
            }
        }

        // Clear History
        binding.clearHistoryOption.setOnClickListener { clearHistory() }

        // Clear Cache
        binding.clearCacheOption.setOnClickListener { clearCache() }

        // Logout
        binding.logoutOption.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            // Extract name from email
            val email = user.email ?: "Unknown"
            val name = email.substringBefore("@").substringBefore(".").replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            // Load from Firebase Database
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val savedName = snapshot.child("name").getValue(String::class.java) ?: name
                    val savedEmail = snapshot.child("email").getValue(String::class.java) ?: email
                    binding.userName.text = savedName
                    binding.userEmail.text = savedEmail
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.userName.text = name
                    binding.userEmail.text = email
                    Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Profile load failed: ${error.message}")
                }
            })

            // Load profile image
            try {
                Glide.with(binding.userImage.context)
                    .load(user.photoUrl ?: R.drawable.user)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(binding.userImage)
            } catch (e: Exception) {
                binding.userImage.setImageResource(R.drawable.user)
                Log.e("ProfileFragment", "Image load failed: ${e.message}")
            }
        } else {
            binding.userName.text = "Guest"
            binding.userEmail.text = "Not signed in"
            binding.userImage.setImageResource(R.drawable.user)
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)

        // Pre-fill current values
        nameEditText.setText(binding.userName.text)
        emailEditText.setText(binding.userEmail.text)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameEditText.text.toString().trim()
                val newEmail = emailEditText.text.toString().trim()
                if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                    updateProfile(newName, newEmail)
                } else {
                    Toast.makeText(requireContext(), "Name and email cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProfile(name: String, email: String) {
        val user = auth.currentUser
        if (user != null) {
            // Update Firebase Database
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
            val updates = mapOf("name" to name, "email" to email)
            userRef.updateChildren(updates)
                .addOnSuccessListener {
                    binding.userName.text = name
                    binding.userEmail.text = email
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Profile update failed: ${it.message}")
                }

            // Update Firebase Auth email
            user.updateEmail(email).addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update email: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Email update failed: ${it.message}")
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Spanish", "Hindi")
        val languageCodes = arrayOf("en", "es", "hi")
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_language, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.language_spinner)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val selectedLanguage = spinner.selectedItemPosition
                setLocale(languageCodes[selectedLanguage])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setLocale(languageCode: String) {
        try {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            requireActivity().recreate()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to change language: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "Language change failed: ${e.message}")
        }
    }

    private fun clearHistory() {
        val user = auth.currentUser
        if (user != null) {
            val favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid).child("favorites")
            favoritesRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to clear history: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Clear history failed: ${it.message}")
                }
        }
    }

    private fun clearCache() {
        try {
            requireContext().cacheDir.deleteRecursively()
            Toast.makeText(requireContext(), "Cache cleared", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to clear cache: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "Clear cache failed: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
