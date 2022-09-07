package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.HistoryDatabase.HistoryEntityDataClass
import com.example.speedometer.R


class HistoryRecycleViewAdapter(private var mContext: Context) :
    RecyclerView.Adapter<HistoryRecycleViewAdapter.WordViewHolder>() {

    private var HistoryEntries: List<HistoryEntityDataClass>? = null

    fun setHistoryRecord(history_List: List<HistoryEntityDataClass>) {
        HistoryEntries = history_List
        notifyDataSetChanged()
    }

    fun getHistoryRecord(): List<HistoryEntityDataClass?>? {
        return HistoryEntries

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.history_viw_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        var historyItem = HistoryEntries?.get(position)

        holder.startPoint.text = historyItem!!.startLocation
        holder.endPoint.text = historyItem!!.endLocation
        holder.tripDate.text = historyItem!!.travelDate
        holder.tripTimeDuration.text = historyItem!!.timeDuration
        holder.tripDistance.text = historyItem!!.traveldDistance.toString() +"\tKm"
        holder.tripAvgSpeed.text = historyItem!!.avgSpeed.toString() +"\tKm/h"
    }

    override fun getItemCount(): Int {
        if (HistoryEntries == null) {
            return 0;
        }
        return HistoryEntries!!.size
    }

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var startPoint: TextView = itemView.findViewById(R.id.startLocation)
        var endPoint:TextView=itemView.findViewById(R.id.endLocation)
        var tripDate:TextView=itemView.findViewById(R.id.date)
        var tripTimeDuration:TextView=itemView.findViewById(R.id.timeDuration)
        var tripDistance:TextView=itemView.findViewById(R.id.travelDistance)
        var tripAvgSpeed:TextView=itemView.findViewById(R.id.avgSpeed)
    }

}