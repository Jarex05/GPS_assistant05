package com.mikhail_R_gps_tracker.gpsassistant.db.brake

import android.os.Parcel
import android.os.Parcelable

class ListItemBrakeChet(): Parcelable {
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

    companion object CREATOR : Parcelable.Creator<ListItemBrakeChet> {
        override fun createFromParcel(parcel: Parcel): ListItemBrakeChet {
            return ListItemBrakeChet(parcel)
        }

        override fun newArray(size: Int): Array<ListItemBrakeChet?> {
            return arrayOfNulls(size)
        }
    }
}