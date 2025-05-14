package com.example.foodies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodies.databinding.FragmentReportProblemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportProblemFragment : Fragment() {

    private var _binding: FragmentReportProblemBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding not initialized")
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportProblemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.submitReportButton.setOnClickListener {
            val issue = binding.issueInput.text.toString().trim()
            if (issue.isNotEmpty()) {
                submitReport(issue)
            } else {
                Toast.makeText(requireContext(), "Please describe the issue", Toast.LENGTH_SHORT).show()
            }
        }
        // Back arrow click to navigate to HomeFragment
        binding.backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ProfileFragment())
                .commit()
        }
    }

    private fun submitReport(issue: String) {
        val user = auth.currentUser
        if (user != null) {
            val reportRef = FirebaseDatabase.getInstance().getReference("Reports").child(user.uid).push()
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val reportData = mapOf(
                "issue" to issue,
                "timestamp" to timestamp,
                "email" to user.email
            )

            reportRef.setValue(reportData)
                .addOnSuccessListener {
                    binding.issueInput.text.clear()
                    Toast.makeText(requireContext(), "Report submitted successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to submit report: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please sign in to submit a report", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
