/*
 * Copyright (c) 2019 Naman Dwivedi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package me.ayra.nusantara.music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ayra.nusantara.music.R
import me.ayra.nusantara.music.constants.Constants.ARTIST
import me.ayra.nusantara.music.constants.Constants.SONG
import me.ayra.nusantara.music.databinding.FragmentLyricsBinding
import me.ayra.nusantara.music.extensions.argument
import me.ayra.nusantara.music.extensions.disposeOnDetach
import me.ayra.nusantara.music.extensions.inflateWithBinding
import me.ayra.nusantara.music.extensions.ioToMain
import me.ayra.nusantara.music.extensions.subscribeForOutcome
import me.ayra.nusantara.music.network.Outcome
import me.ayra.nusantara.music.network.api.LyricsRestService
import me.ayra.nusantara.music.ui.fragments.base.BaseNowPlayingFragment
import me.ayra.nusantara.music.util.AutoClearedValue
import org.koin.android.ext.android.inject

class LyricsFragment : BaseNowPlayingFragment() {
    companion object {
        fun newInstance(artist: String, title: String): LyricsFragment {
            return LyricsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARTIST, artist)
                    putString(SONG, title)
                }
            }
        }
    }

    private lateinit var artistName: String
    lateinit var songTitle: String
    var binding by AutoClearedValue<FragmentLyricsBinding>(this)

    private val lyricsService by inject<LyricsRestService>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_lyrics, container)
        artistName = argument(ARTIST)
        songTitle = argument(SONG)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.songTitle = songTitle

        // TODO make the lyrics handler/repo injectable
        lyricsService.getLyrics(artistName, songTitle)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when (outcome) {
                        is Outcome.Success -> binding.lyrics = outcome.data
                    }
                }
                .disposeOnDetach(view)
    }
}
