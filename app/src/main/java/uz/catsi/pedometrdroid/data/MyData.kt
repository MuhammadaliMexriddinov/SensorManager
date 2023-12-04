package uz.catsi.pedometrdroid.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class MyData(
    var step: String,
    var calory:String,
    var distance:String
):Parcelable
