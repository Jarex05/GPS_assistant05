package com.mikhail_R_gps_tracker.gpsassistant.db.redacktor

import android.os.Parcel
import android.os.Parcelable

class ListItemRedacktorChet(): Parcelable {
    var idChet: Int = 0
    var startChet: Int = 0
    var picketStartChet: Int = 0
    var minusChet = "empty"
    var plusChet = "empty"

    constructor(parcel: Parcel) : this() {
        startChet = parcel.readInt()
        picketStartChet = parcel.readInt()
        minusChet = parcel.readString().toString()
        plusChet = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(startChet)
        dest.writeInt(picketStartChet)
        dest.writeString(minusChet)
        dest.writeString(plusChet)
    }

    companion object CREATOR : Parcelable.Creator<ListItemRedacktorChet> {
        override fun createFromParcel(parcel: Parcel): ListItemRedacktorChet {
            return ListItemRedacktorChet(parcel)
        }

        override fun newArray(size: Int): Array<ListItemRedacktorChet?> {
            return arrayOfNulls(size)
        }
    }
}