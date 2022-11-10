package com.tabata.deadlinemonitor.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.tabata.deadlinemonitor.MyCaptureActivity
import com.tabata.deadlinemonitor.database.ItemInfo
import com.tabata.deadlinemonitor.database.ItemInfoDatabase
import com.tabata.deadlinemonitor.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding設定
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        // DB, ViewModel, Lifecycle設定
        val application = requireNotNull(this.activity).application
        val dataSource = ItemInfoDatabase.getInstance(application).itemInfoDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        val homeViewModel = ViewModelProvider(
            this, viewModelFactory
        )[HomeViewModel::class.java]
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 読み取りボタンのリスナー設定
        binding.scanButton.setOnClickListener {
            scanCode()
        }

        // アイテムリストの設定
        homeViewModel.updateCheckItemList()
        val itemListView = binding.itemListView
        itemListView.layoutManager = LinearLayoutManager(requireContext())

        // アイテムリストのオブザーバー
        homeViewModel.dataSet.observe(viewLifecycleOwner) {
            val dataSet = homeViewModel.dataSet.value
            val itemListViewAdapter = ItemListViewAdapter(dataSet!!)
            itemListView.adapter = itemListViewAdapter

            // アイテムリストのセルのリスナー
            itemListViewAdapter.setOnItemCellClickListener(
                object : ItemListViewAdapter.OnItemCellClickListener {
                    override fun onItemClick(itemInfo: ItemInfo) {
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToItemInfoFragment(itemInfo.janCode)
                        )
                    }
                }
            )
        }

        return binding.root
    }

    private fun scanCode() {
        val options = setScanOption()
        barLauncher.launch(options)
    }

    private fun setScanOption(): ScanOptions {
        val options = ScanOptions()

        // アクティビティの指定
        options.captureActivity = MyCaptureActivity::class.java
        // スキャン時にビープ音を鳴らす
        options.setBeepEnabled(true)
        // 13桁,8桁のJAN(EAN)コードのみスキャンする
        options.setDesiredBarcodeFormats(ScanOptions.EAN_13, ScanOptions.EAN_8)
        // スキャン画面下の説明
        options.setPrompt("赤い線をバーコードに合わせてください")

        return options
    }

    private val barLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            val janCode = result.contents
            findNavController().navigate(
                HomeFragmentDirections
                    .actionHomeFragmentToItemInfoFragment(janCode)
            )
        }
    }
}