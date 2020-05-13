package com.haroldadmin.sampleapp.about

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAboutBinding
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.renderState

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        renderState(AboutState()) { state ->
            ObjectAnimator
                .ofFloat(binding.logo, View.ROTATION, 0f, 360f)
                .apply {
                    duration = 3000
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }
                .also { it.start() }

            binding.debugInfo.text = getString(R.string.debugInformation, state.appVersion, state.libraryVersion)
        }

        return binding.root
    }
}