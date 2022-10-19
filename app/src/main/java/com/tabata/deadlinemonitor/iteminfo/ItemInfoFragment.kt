package com.tabata.deadlinemonitor.iteminfo

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tabata.deadlinemonitor.R
import com.tabata.deadlinemonitor.database.ItemInfoDatabase
import com.tabata.deadlinemonitor.databinding.FragmentItemInfoBinding
import java.util.*

class ItemInfoFragment : Fragment(), DatePicker.OnDateChangedListener, NumberPicker.OnValueChangeListener {

    private var _binding: FragmentItemInfoBinding? = null
    private val binding get() = _binding!!

    private val args: ItemInfoFragmentArgs by navArgs()
    private lateinit var itemInfoViewModel: ItemInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding設定
        _binding = FragmentItemInfoBinding.inflate(inflater, container, false)

        // DB, ViewModel, Lifecycle設定
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

        // JANの重複確認
        itemInfoViewModel.isEntityDuplicate(args.janCode)

        // 重複の有無で商品情報画面を変更
        itemInfoViewModel.isExist.observe(viewLifecycleOwner) {
            if (it) {
                val itemInfo = itemInfoViewModel.itemInfo.value
                if (itemInfo != null) {
                    binding.itemName.setText(itemInfo.itemName)

                    val calendar = Calendar.getInstance()
                    calendar.time = itemInfo.deadlineDate!!
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    binding.datePicker.init(year, month, day, this)
                    binding.yearPicker.value = year
                    binding.monthPicker.value = month + 1

                    if (itemInfo.isChecked == 1) {
                        binding.pickerSwitch.isChecked = true
                    }

                    binding.checkCycleSpinner.setSelection(itemInfo.checkCycle - 1)

                    itemInfoViewModel.deadlineDate = calendar.time
                }
                binding.registerButton.text = "更新"
            } else {
                // 年月のPickerの初期値設定
                val calendar = Calendar.getInstance()
                binding.yearPicker.value = calendar.get(Calendar.YEAR)
                binding.monthPicker.value = calendar.get(Calendar.MONTH) + 1

                binding.registerButton.text = "登録"
            }
        }

        // スピナーの設定
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            (1..12).toList()
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

        // 年月のみのPickerの設定
        binding.yearPicker.let {
            it.maxValue = 2100
            it.minValue = 1900
            it.setOnValueChangedListener(this)
        }
        binding.monthPicker.let {
            it.maxValue = 12
            it.minValue = 1
            it.setOnValueChangedListener(this)
        }

        // Pickerの取得モード変更スイッチの設定
        val switch = binding.pickerSwitch
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.datePicker.visibility = View.INVISIBLE
                binding.monthYearPicker.visibility = View.VISIBLE
                itemInfoViewModel.onCheck()
            } else {
                binding.datePicker.visibility = View.VISIBLE
                binding.monthYearPicker.visibility = View.INVISIBLE
                itemInfoViewModel.offCheck()
            }
        }

        // 登録ボタンのオブザーバー
        itemInfoViewModel.registerEvent.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                    ItemInfoFragmentDirections.actionItemInfoFragmentToHomeFragment()
                )
                itemInfoViewModel.doneRegister()
            }
        }

        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("商品情報の削除")
                .setMessage("削除したデータは2度と戻りません\n本当に削除してもよろしいですか？")
                .setIcon(R.drawable.ic_warning_icon)
                .setPositiveButton("削除") { _, _ ->
                    // OKを押したときの処理を書く
                    itemInfoViewModel.delete(itemInfoViewModel.itemInfo.value!!)
                    findNavController()
                        .navigate(
                            ItemInfoFragmentDirections
                                .actionItemInfoFragmentToHomeFragment()
                        )
                }
                .setNegativeButton("キャンセル") { _, _ ->
                    // Cancelを押したときの処理を書く
                }
                .show()
        }

        binding.cancelButton.setOnClickListener {
            findNavController()
                .navigate(
                    ItemInfoFragmentDirections
                        .actionItemInfoFragmentToHomeFragment()
                )
        }
        return binding.root
    }

    // DatePickerが操作されたときに実行
    override fun onDateChanged(view: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        itemInfoViewModel.deadlineDate = calendar.time
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onValueChange(picker: NumberPicker?, old: Int, new: Int) {
        if (picker == binding.monthPicker) {
            if (old == 12 && new == 1) {
                binding.yearPicker.value += 1
            }
            if (old == 1 && new == 12) {
                binding.yearPicker.value -= 1
            }
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, binding.yearPicker.value)
        calendar.set(Calendar.MONTH, binding.monthPicker.value - 1)
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DATE)   // 月末の日付を取得
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
        itemInfoViewModel.deadlineDate = calendar.time
    }
}