package com.mikhail_R_gps_tracker.gpsassistant.db.limitations

import android.os.Parcel
import android.os.Parcelable

class ListItemLimitationsChet(): Parcelable {
    var idChet: Int = 0
    var startChet: Int = 0
    var picketStartChet: Int = 0
    var finishChet: Int = 0
    var picketFinishChet: Int = 0
    var speedChet: Int = 0

    constructor(parcel: Parcel) : this() {
        startChet = parcel.readInt()
        picketStartChet = parcel.readInt()
        finishChet = parcel.readInt()
        picketFinishChet = parcel.readInt()
        speedChet = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(startChet)
        dest.writeInt(picketStartChet)
        dest.writeInt(finishChet)
        dest.writeInt(picketFinishChet)
        dest.writeInt(speedChet)
    }

    companion object CREATOR : Parcelable.Creator<ListItemLimitationsChet> {
        override fun createFromParcel(parcel: Parcel): ListItemLimitationsChet {
            return ListItemLimitationsChet(parcel)
        }

        override fun newArray(size: Int): Array<ListItemLimitationsChet?> {
            return arrayOfNulls(size)
        }
    }
}