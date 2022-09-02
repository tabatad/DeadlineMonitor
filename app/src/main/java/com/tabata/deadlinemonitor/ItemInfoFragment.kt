package com.tabata.deadlinemonitor

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tabata.deadlinemonitor.databinding.FragmentItemInfoBinding
import java.util.*

class ItemInfoFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var dataPickerText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentItemInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_item_info, container, false
        )

//        dataPickerText = binding.datePickerText
//
//        binding.datePickerText.setOnClickListener {
//            showDatePickerDialog()
//        }

        val spinnerItems = (1..12).toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        binding.checkCycleSpinner.adapter = adapter

        return binding.root
    }

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val context = context

        if (context != null) {
            DatePickerDialog(
                context,
                this,
                year, month, day
            ).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val showText = "${year}年${month + 1}月${day}日"
        dataPickerText.text = showText
    }

}