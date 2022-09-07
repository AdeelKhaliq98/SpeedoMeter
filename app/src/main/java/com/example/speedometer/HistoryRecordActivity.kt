package com.example.speedometer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.HistoryRecycleViewAdapter
import com.example.Helper.provideHistoryViewModelFactory
import com.example.HistoryDatabase.HistoryEntityDataClass
import com.example.ViewModels.HistoryViewModel
import com.example.speedometer.databinding.ActivityHistoryRecordBinding
import kotlinx.coroutines.launch

class HistoryRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryRecordBinding

    private var historyViewModel: HistoryViewModel? = null
    private lateinit var historyRecycleViewAdapter: HistoryRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHistoryRecordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        historyRecycleViewAdapter = HistoryRecycleViewAdapter(this)
        binding.historyRecycleview.adapter = historyRecycleViewAdapter
        binding.historyRecycleview.layoutManager = LinearLayoutManager(this)


        historyViewModel = ViewModelProvider(
            this,
            provideHistoryViewModelFactory()).get(HistoryViewModel::class.java)

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                if (swipeDir == 1 shl 3) {
                    lifecycleScope.launch {
                        val getHistoryData: HistoryEntityDataClass? =
                            historyRecycleViewAdapter.getHistoryRecord()?.get(viewHolder.adapterPosition)
                        if (getHistoryData != null) {
                            historyViewModel?.deleteHistory(getHistoryData)
                        }
                    }
                }
            }
        }).attachToRecyclerView(binding.historyRecycleview)
        setupViewModel()
    }

    private fun setupViewModel() {
        historyViewModel?.getHistoryRecord()?.observe(this,
            Observer<List<Any?>?> { HistoryEntries ->
                historyRecycleViewAdapter.setHistoryRecord(HistoryEntries as List<HistoryEntityDataClass>)
            })
    }
}