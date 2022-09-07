package com.example.HistoryDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "historyList")
data class HistoryEntityDataClass(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var history_local_id: Int=0,
    var startLocation: String?=null,
    var endLocation: String?=null,
    var travelDate: String?=null,
    var traveldDistance: Double?=0.0,
    var timeDuration: String?=null,
    var avgSpeed: Int ?=0,
    @Ignore var accurateSpeed:Int=0,
    @Ignore var MaxSpeed:Int=0,
    @Ignore var travelDistance:Double=0.0,
    @Ignore var averageSpeed:Int=0,
    @Ignore var accuracy:Int=0,
    @Ignore var time:String?=null,
    @Ignore var lat: Double = 0.0,
    @Ignore var log: Double = 0.0
) {
    constructor() : this(0, "", "", "", 0.0, "", 0)
}
