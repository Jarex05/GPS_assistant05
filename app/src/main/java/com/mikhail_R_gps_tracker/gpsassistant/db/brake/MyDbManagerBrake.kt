package com.mikhail_R_gps_tracker.gpsassistant.db.brake

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbHelper
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManagerBrake(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }

    // Функции для торможения четных поездов

    fun insertToDbBrakeChet(startBrakeChet: Int, picketStartBrakeChet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_BRAKE_CHET, startBrakeChet)
            put(MyDbNameClass.COLUMN_PICKET_START_BRAKE_CHET, picketStartBrakeChet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_BRAKE_CHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataBrakeChet() : ArrayList<ListItemBrakeChet> = withContext(Dispatchers.IO) {
        val dataListBrakeChet = ArrayList<ListItemBrakeChet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_BRAKE_CHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartBrakeChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_BRAKE_CHET))
            val dataPicketStartBrakeChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_BRAKE_CHET))
            val dataIdBrakeChet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemBrakeChet = ListItemBrakeChet()
            itemBrakeChet.startChet = dataStartBrakeChet
            itemBrakeChet.picketStartChet = dataPicketStartBrakeChet
            itemBrakeChet.idChet = dataIdBrakeChet
            dataListBrakeChet.add(itemBrakeChet)
        }
        cursor.close()

        return@withContext dataListBrakeChet
    }

    fun updateDbDataBrakeChet(startBrakeChet: Int, picketStartBrakeChet: Int, idBrakeChet: Int){
        val selection = BaseColumns._ID + "=$idBrakeChet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_BRAKE_CHET, startBrakeChet)
            put(MyDbNameClass.COLUMN_PICKET_START_BRAKE_CHET, picketStartBrakeChet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_BRAKE_CHET, values, selection, null)
    }

    fun deleteDbDataBrakeChet(idBrakeChet: Int){
        val selection = BaseColumns._ID + "=$idBrakeChet"
        db?.delete(MyDbNameClass.TABLE_NAME_BRAKE_CHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//

    // Функции для торможения нечетных поездов

    fun insertToDbBrakeNechet(startBrakeNechet: Int, picketStartBrakeNechet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_BRAKE_NECHET, startBrakeNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_BRAKE_NECHET, picketStartBrakeNechet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_BRAKE_NECHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataBrakeNechet() : ArrayList<ListItemBrakeNechet> = withContext(Dispatchers.IO) {
        val dataListBrakeNechet = ArrayList<ListItemBrakeNechet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_BRAKE_NECHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartBrakeNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_BRAKE_NECHET))
            val dataPicketStartBrakeNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_BRAKE_NECHET))
            val dataIdBrakeNechet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemBrakeNechet = ListItemBrakeNechet()
            itemBrakeNechet.startNechet = dataStartBrakeNechet
            itemBrakeNechet.picketStartNechet = dataPicketStartBrakeNechet
            itemBrakeNechet.idNechet = dataIdBrakeNechet
            dataListBrakeNechet.add(itemBrakeNechet)
        }
        cursor.close()

        return@withContext dataListBrakeNechet
    }

    fun updateDbDataBrakeNechet(startBrakeNechet: Int, picketStartBrakeNechet: Int, idBrakeNechet: Int){
        val selection = BaseColumns._ID + "=$idBrakeNechet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_BRAKE_NECHET, startBrakeNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_BRAKE_NECHET, picketStartBrakeNechet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_BRAKE_NECHET, values, selection, null)
    }

    fun deleteDbDataBrakeNechet(idBrakeNechet: Int){
        val selection = BaseColumns._ID + "=$idBrakeNechet"
        db?.delete(MyDbNameClass.TABLE_NAME_BRAKE_NECHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//



    fun  closeDb(){
        myDbHelper.close()
    }
}