package com.tabata.deadlinemonitor.home

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.tabata.deadlinemonitor.MyCaptureActivity
import com.tabata.deadlinemonitor.R
import com.tabata.deadlinemonitor.database.ItemInfo
import com.tabata.deadlinemonitor.database.ItemInfoDatabase
import com.tabata.deadlinemonitor.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding設定
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // DB, ViewModel, Lifecycle設定
        val application = requireNotNull(this.activity).application
        val dataSource = ItemInfoDatabase.getInstance(application).itemInfoDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        homeViewModel = ViewModelProvider(
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

        // レイアウトをタップするとフォーカスを要求
        binding.homeFragmentLayout.setOnClickListener {
            it.requestFocus()
        }

        // EditTextからフォーカスが外れるとキーボードを非表示
        binding.searchByJanText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                showoffKeyboard()
            }
        }

        // キーボードの確定ボタンがタップされたときの挙動
        binding.searchByJanText.setOnEditorActionListener { view, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE ||
                keyEvent != null &&
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                if (homeViewModel.isItemExist(view.text.toString())) {
                    findNavController().navigate(
                        HomeFragmentDirections
                            .actionHomeFragmentToItemInfoFragment(binding.searchByJanText.text.toString())
                    )
                } else {
                    showDialog(view.text.toString())
                }
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
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

    // キーボードを非表示にする
    private fun showoffKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.homeFragmentLayout.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showDialog(janCode: String) {
        AlertDialog.Builder(context)
            .setTitle("JAN: $janCode")
            .setMessage("登録されていない商品です\n新たに商品を登録しますか？")
            .setIcon(R.drawable.ic_warning_icon)
            .setPositiveButton("登録に進む") { _, _ ->
                findNavController().navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToItemInfoFragment(janCode)
                )
            }
            .setNegativeButton("キャンセル") { _, _ ->
                // キャンセルを押したときの処理を書く
            }
            .show()
    }

    // 商品情報画面から戻ってきたときにEditTextを初期化
    override fun onStop() {
        super.onStop()
        binding.searchByJanText.text = null
    }
}