package com.example.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.fragments.FavouriteTracksFragment
import com.example.playlistmaker.media.ui.fragments.PlaylistsFragment

class MediaViewPagerAdapter(activity: MediaActivity) : FragmentStateAdapter(activity) { // стандартный адаптер для ViewPager2

    override fun getItemCount(): Int = 2 // количество вкладок (фрагментов)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavouriteTracksFragment.newInstance() // вкладка с избранными треками
            else -> PlaylistsFragment.newInstance() // вкладка с плейлистами
        }
    }
}