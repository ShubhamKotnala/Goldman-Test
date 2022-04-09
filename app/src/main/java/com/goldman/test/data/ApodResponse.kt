package com.goldman.test.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "apod_list")
data class ApodResponse(
    @ColumnInfo(name = "copyright") @SerializedName("copyright") var copyright: String = "",
    @PrimaryKey @ColumnInfo(name = "date") @SerializedName("date") var date: String = "",
    @ColumnInfo(name = "explanation") @SerializedName("explanation") var explanation: String = "",
    @ColumnInfo(name = "hdurl") @SerializedName("hdurl") var hdurl: String = "",
    @ColumnInfo(name = "media_type") @SerializedName("media_type") var mediaType: String = "",
    @ColumnInfo(name = "service_version") @SerializedName("service_version") var serviceVersion: String = "",
    @ColumnInfo(name = "title") @SerializedName("title") var title: String = "",
    @ColumnInfo(name = "url") @SerializedName("url") var url: String = ""

) : Serializable

data class ApodError(@SerializedName("code") var code: String = "",
                     @SerializedName("msg") var msg: String = "")