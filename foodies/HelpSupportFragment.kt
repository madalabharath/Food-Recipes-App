package com.example.foodies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodies.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment() {

    private var _binding: FragmentHelpSupportBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:foodierecipes@foodierecipeapp.com")
                putExtra(Intent.EXTRA_SUBJECT, "Foodie Recipe App Support")
            }
            startActivitySafely(intent, "Email client not found")
        }

        binding.visitWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.foodierecipeapp.com"))
            startActivitySafely(intent, "Web browser not found")
        }

        binding.callSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+1234567890"))
            startActivitySafely(intent, "Dialer not found")
        }
        // Back arrow click to navigate to HomeFragment
        binding.backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ProfileFragment())
                .commit()
        }
    }

    private fun startActivitySafely(intent: Intent, errorMessage: String) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
