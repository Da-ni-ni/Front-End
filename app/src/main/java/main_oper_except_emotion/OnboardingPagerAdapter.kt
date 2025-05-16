package main_oper_except_emotion

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(fragment: FragmentActivity, private val titles: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(titles[position])
    }
}