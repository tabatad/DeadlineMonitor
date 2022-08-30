package com.tabata.deadlinemonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
            val builder =
                AlertDialog.Builder(requireContext())
            builder.setTitle("Result")
            builder.setMessage(result.contents)
            builder.setPositiveButton(
                "OK"
            ) { dialogInterface, _ -> dialogInterface.dismiss() }.show()
        }
    }
}