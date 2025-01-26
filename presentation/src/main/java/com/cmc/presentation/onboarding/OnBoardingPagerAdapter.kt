package com.cmc.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cmc.presentation.R

class OnBoardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingPageFragment.newInstance(
                "사진 스팟,\n아직도 발품 팔아요?",
                "놓치기 아까운 스팟, 파티타엔 다 있어요!",
                R.drawable.img_onboarding_1,
                false,
                45
            )
            1 -> OnBoardingPageFragment.newInstance(
                "추천과 검색을 통해\n최고의 스팟을 찾아봐요!",
                "놓치기 아까운 스팟, 파티타엔 다 있어요!",
                R.drawable.img_onboarding_2,
                false,
                0
            )
            else -> OnBoardingPageFragment.newInstance(
                "나만 아는 숨은 스팟을\n등록하고 공유해봐요",
                "놓치기 아까운 스팟, 파티타엔 다 있어요!",
                R.drawable.img_onboarding_3,
                true,
                80
            )
        }
    }
}
