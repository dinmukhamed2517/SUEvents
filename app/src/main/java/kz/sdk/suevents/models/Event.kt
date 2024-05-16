package kz.sdk.suevents.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id:String? = null,
    var title:String? = null,
    var img:String? = null,
    var date:String? = null,
    var time:String? = null,
    var description:String? = null,
    var type:String? = null,
    var category:String? = null,
): Parcelable