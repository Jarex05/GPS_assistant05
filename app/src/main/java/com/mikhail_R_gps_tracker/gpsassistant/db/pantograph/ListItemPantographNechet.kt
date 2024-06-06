package com.mikhail_R_gps_tracker.gpsassistant.db.pantograph

import android.os.Parcel
import android.os.Parcelable

class ListItemPantographNechet(): Parcelable {
    var idNechet: Int = 0
    var startNechet: Int = 0
    var picketStartNechet: Int = 0

    constructor(parcel: Parcel) : this() {
        startNechet = parcel.readInt()
        picketStartNechet = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(startNechet)
        dest.writeInt(picketStartNechet)
    }

    companion object CREATOR : Parcelable.Creator<ListItemPantographNechet> {
        override fun createFromParcel(parcel: Parcel): ListItemPantographNechet {
            return ListItemPantographNechet(parcel)
        }

        override fun newArray(size: Int): Array<ListItemPantographNechet?> {
            return arrayOfNulls(size)
        }
    }
}