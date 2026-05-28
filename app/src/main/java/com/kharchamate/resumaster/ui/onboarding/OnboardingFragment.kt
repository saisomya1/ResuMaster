package com.kharchamate.resumaster.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kharchamate.resumaster.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleRes = arguments?.getInt(ARG_TITLE) ?: 0
        val descRes = arguments?.getInt(ARG_DESC) ?: 0
        val imageRes = arguments?.getInt(ARG_IMAGE) ?: 0

        if (titleRes != 0) {
            binding.tvTitle.text = androidx.core.text.HtmlCompat.fromHtml(getString(titleRes), androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        if (descRes != 0) binding.tvDescription.setText(descRes)
        if (imageRes != 0) binding.ivIllustration.setImageResource(imageRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(page: OnboardingPage): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle().apply {
                putInt(ARG_TITLE, page.titleRes)
                putInt(ARG_DESC, page.descriptionRes)
                putInt(ARG_IMAGE, page.imageRes)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
