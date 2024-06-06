package com.mikhail_R_gps_tracker.gpsassistant.db.pantograph

import android.os.Parcel
import android.os.Parcelable

class ListItemPantographChet(): Parcelable {
    var idChet: Int = 0
    var startChet: Int = 0
    var picketStartChet: Int = 0

    constructor(parcel: Parcel) : this() {
        startChet = parcel.readInt()
        picketStartChet = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(startChet)
        dest.writeInt(picketStartChet)
    }

    companion object CREATOR : Parcelable.Creator<ListItemPantographChet> {
        override fun createFromParcel(parcel: Parcel): ListItemPantographChet {
            return ListItemPantographChet(parcel)
        }

        override fun newArray(size: Int): Array<ListItemPantographChet?> {
            return arrayOfNulls(size)
        }
    }
}