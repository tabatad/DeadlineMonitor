package com.tabata.deadlinemonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.tabata.deadlinemonitor.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        binding.scanButton.setOnClickListener {
            scanCode()
        }

        binding.goItemInfoButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToItemInfoFragment("sample"))
        }

        return binding.root
    }

    private fun scanCode() {
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.captureActivity = MyCaptureActivity::class.java
        options.setOrientationLocked(false)
        barLauncher.launch(options)
    }

    private val barLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            val janCode = result.contents
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToItemInfoFragment(janCode))
        }
    }
}