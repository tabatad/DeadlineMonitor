package com.tabata.deadlinemonitor.iteminfo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.tabata.deadlinemonitor.R
import com.tabata.deadlinemonitor.database.ItemInfoDatabase
import com.tabata.deadlinemonitor.databinding.FragmentItemInfoBinding
import java.util.*

class ItemInfoFragment : Fragment(), DatePicker.OnDateChangedListener {

    private val args: ItemInfoFragmentArgs by navArgs()
    lateinit var itemInfoViewModel: ItemInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding設定
        val binding: FragmentItemInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_item_info, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = ItemInfoDatabase.getInstance(application).itemInfoDao
        val viewModelFactory = ItemInfoViewModelFactory(dataSource, application)
        itemInfoViewModel = ViewModelProvider(
            this, viewModelFactory
        )[ItemInfoViewModel::class.java]
        binding.itemInfoViewModel = itemInfoViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // スキャンしたJANの表示
        itemInfoViewModel.janCode.value = args.janCode

        // スピナーの設定
        val spinnerItems = (1..12).toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        binding.checkCycleSpinner.adapter = adapter

        // スピナーのリスナーの設定
        binding.checkCycleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent != null) {
                        itemInfoViewModel.checkCycle = parent.selectedItem.toString().toInt()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // DatePickerのリスナーの設定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.datePicker.setOnDateChangedListener(this)
        }

        // 登録ボタンのオブザーバー
        // insert後に実行
        itemInfoViewModel.registerEvent.observe(viewLifecycleOwner) {
            if (it == true) {
                val text: String = if (itemInfoViewModel.isInsertComplete) {
                    "登録完了"
                } else {
                    "重複するデータです"
                }
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    text,
                    Snackbar.LENGTH_SHORT
                ).show()

                this.findNavController().navigate(
                    ItemInfoFragmentDirections.actionItemInfoFragmentToHomeFragment()
                )
                itemInfoViewModel.doneRegister()
            }
        }

        return binding.root
    }

    // DatePickerが操作されたときに実行
    override fun onDateChanged(view: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        itemInfoViewModel.deadlineDate = calendar.time
    }
}