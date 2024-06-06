package com.mikhail_R_gps_tracker.gpsassistant.db.limitations

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbHelper
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManagerLimitations(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }

    // Функции для ограничения скорости четных поездов

    fun insertToDbLimitationsChet(startLimitationsChet: Int, picketStartLimitationsChet: Int, finishLimitationsChet: Int, picketFinishLimitationsChet: Int, speedLimitationsChet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_LIMITATIONS_CHET, startLimitationsChet)
            put(MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_CHET, picketStartLimitationsChet)
            put(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_CHET, finishLimitationsChet)
            put(MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_CHET, picketFinishLimitationsChet)
            put(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_CHET, speedLimitationsChet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_LIMITATIONS_CHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataLimitationsChet() : ArrayList<ListItemLimitationsChet> = withContext(Dispatchers.IO) {
        val dataListLimitationsChet = ArrayList<ListItemLimitationsChet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_LIMITATIONS_CHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_LIMITATIONS_CHET))
            val dataPicketStartLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(
                MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_CHET
            ))
            val dataFinishLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_CHET))
            val dataPicketFinishLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(
                MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_CHET
            ))
            val dataSpeedLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_CHET))
            val dataIdLimitationsChet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemLimitationsChet = ListItemLimitationsChet()
            itemLimitationsChet.startChet = dataStartLimitationsChet
            itemLimitationsChet.picketStartChet = dataPicketStartLimitationsChet
            itemLimitationsChet.finishChet = dataFinishLimitationsChet
            itemLimitationsChet.picketFinishChet = dataPicketFinishLimitationsChet
            itemLimitationsChet.speedChet = dataSpeedLimitationsChet
            itemLimitationsChet.idChet = dataIdLimitationsChet
            dataListLimitationsChet.add(itemLimitationsChet)
        }
        cursor.close()

        return@withContext dataListLimitationsChet
    }

    fun updateDbDataLimitationsChet(startLimitationsChet: Int, picketStartLimitationsChet: Int, finishLimitationsChet: Int, picketFinishLimitationsChet: Int, speedLimitationsChet: Int, idLimitationsChet: Int){
        val selection = BaseColumns._ID + "=$idLimitationsChet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_LIMITATIONS_CHET, startLimitationsChet)
            put(MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_CHET, picketStartLimitationsChet)
            put(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_CHET, finishLimitationsChet)
            put(MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_CHET, picketFinishLimitationsChet)
            put(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_CHET, speedLimitationsChet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_LIMITATIONS_CHET, values, selection, null)
    }

    fun deleteDbDataLimitationsChet(idLimitationsChet: Int){
        val selection = BaseColumns._ID + "=$idLimitationsChet"
        db?.delete(MyDbNameClass.TABLE_NAME_LIMITATIONS_CHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//

    // Функции для ограничения скорости нечетных поездов

    fun insertToDbLimitationsNechet(startLimitationsNechet: Int, picketStartLimitationsNechet: Int, finishLimitationsNechet: Int, picketFinishLimitationsNechet: Int, speedLimitationsNechet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_LIMITATIONS_NECHET, startLimitationsNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_NECHET, picketStartLimitationsNechet)
            put(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_NECHET, finishLimitationsNechet)
            put(MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_NECHET, picketFinishLimitationsNechet)
            put(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_NECHET, speedLimitationsNechet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_LIMITATIONS_NECHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataLimitationsNechet() : ArrayList<ListItemLimitationsNechet> = withContext(Dispatchers.IO) {
        val dataListLimitationsNechet = ArrayList<ListItemLimitationsNechet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_LIMITATIONS_NECHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartLimitationsNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_LIMITATIONS_NECHET))
            val dataPicketStartLimitationsNechet: Int = cursor.getInt(cursor.getColumnIndex(
                MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_NECHET
            ))
            val dataFinishLimitationsNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_NECHET))
            val dataPicketFinishLimitationsNechet: Int = cursor.getInt(cursor.getColumnIndex(
                MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_NECHET
            ))
            val dataSpeedLimitationsNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_NECHET))
            val dataIdLimitationsNechet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemLimitationsNechet = ListItemLimitationsNechet()
            itemLimitationsNechet.startNechet = dataStartLimitationsNechet
            itemLimitationsNechet.picketStartNechet = dataPicketStartLimitationsNechet
            itemLimitationsNechet.finishNechet = dataFinishLimitationsNechet
            itemLimitationsNechet.picketFinishNechet = dataPicketFinishLimitationsNechet
            itemLimitationsNechet.speedNechet = dataSpeedLimitationsNechet
            itemLimitationsNechet.idNechet = dataIdLimitationsNechet
            dataListLimitationsNechet.add(itemLimitationsNechet)
        }
        cursor.close()

        return@withContext dataListLimitationsNechet
    }

    fun updateDbDataLimitationsNechet(startLimitationsNechet: Int, picketStartLimitationsNechet: Int, finishLimitationsNechet: Int, picketFinishLimitationsNechet: Int, speedLimitationsNechet: Int, idLimitationsNechet: Int){
        val selection = BaseColumns._ID + "=$idLimitationsNechet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_LIMITATIONS_NECHET, startLimitationsNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_NECHET, picketStartLimitationsNechet)
            put(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_NECHET, finishLimitationsNechet)
            put(MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_NECHET, picketFinishLimitationsNechet)
            put(MyDbNameClass.COLUMN_SPEED_LIMITATIONS_NECHET, speedLimitationsNechet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_LIMITATIONS_NECHET, values, selection, null)
    }

    fun deleteDbDataLimitationsNechet(idLimitationsNechet: Int){
        val selection = BaseColumns._ID + "=$idLimitationsNechet"
        db?.delete(MyDbNameClass.TABLE_NAME_LIMITATIONS_NECHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//



    fun  closeDb(){
        myDbHelper.close()
    }
}