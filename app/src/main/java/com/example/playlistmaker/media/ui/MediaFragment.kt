package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.mediaLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager() // функция для работы с медиатором для tabLayout и viewPager)
    }

    private fun setupViewPager() {
        val adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        // TabLayoutMediator
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_favourite_tracks)
                1 -> tab.text = getString(R.string.tab_playlist)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Освобождаем медиатор, чтобы не было утечек
        tabMediator.detach()
        _binding = null
    }
}