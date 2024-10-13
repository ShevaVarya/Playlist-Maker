package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaActivity : AppCompatActivity() {

    private val viewModel by viewModel<MediaViewModel>()

    private val binding: ActivityMediaBinding by lazy {
        ActivityMediaBinding.inflate(layoutInflater)
    }

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.favourite_tracks)
                    }

                    1 -> {
                        tab.text = getString(R.string.playlists)
                    }
                }
            }
        tabLayoutMediator.attach()
    }
}