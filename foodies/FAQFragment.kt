package com.example.foodies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodies.databinding.FragmentFAQBinding
import com.google.android.material.card.MaterialCardView

class FAQFragment : Fragment() {

    private var _binding: FragmentFAQBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFAQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Expand/Collapse FAQ items
        setupExpandableFAQ(binding.faqCard1, binding.faqAnswer1)
        setupExpandableFAQ(binding.faqCard2, binding.faqAnswer2)
        setupExpandableFAQ(binding.faqCard3, binding.faqAnswer3)
        setupExpandableFAQ(binding.faqCard4, binding.faqAnswer4)
    }


    private fun setupExpandableFAQ(card: MaterialCardView, answer: View) {
        card.setOnClickListener {
            answer.visibility = if (answer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
