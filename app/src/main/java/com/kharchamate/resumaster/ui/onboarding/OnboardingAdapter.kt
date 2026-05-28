package com.kharchamate.resumaster.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(
    activity: FragmentActivity,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(pages[position])
    }
}
