package com.tabata.deadlinemonitor.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tabata.deadlinemonitor.R
import com.tabata.deadlinemonitor.database.ItemInfo
import java.text.SimpleDateFormat
import java.util.Locale

class ItemListViewAdapter(private val dataSet: List<ItemInfo>) :
    RecyclerView.Adapter<ItemListViewAdapter.ViewHolder>() {

    private lateinit var listener: OnItemCellClickListener

    interface OnItemCellClickListener {
        fun onItemClick(itemInfo: ItemInfo)
    }

    fun setOnItemCellClickListener(listener: OnItemCellClickListener) {
        this.listener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val janCode: TextView
        val deadlineDate: TextView

        init {
            itemName = view.findViewById(R.id.item_list_item_name)
            janCode = view.findViewById(R.id.item_list_jan_code)
            deadlineDate = view.findViewById(R.id.item_list_deadline_date)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_cell, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemName.text = dataSet[position].itemName
        viewHolder.janCode.text = dataSet[position].janCode

        if (dataSet[position].isChecked == 1) {
            viewHolder.deadlineDate.text = SimpleDateFormat(
                "yyyy/MM",
                Locale.getDefault()
            ).format(dataSet[position].deadlineDate!!)
        } else {
            viewHolder.deadlineDate.text = SimpleDateFormat(
                "yyyy/MM/dd",
                Locale.getDefault()
            ).format(dataSet[position].deadlineDate!!)
        }

        viewHolder.itemView.setOnClickListener {
            listener.onItemClick(dataSet[position])
        }
    }

    override fun getItemCount() = dataSet.size
}