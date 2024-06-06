package com.mikhail_R_gps_tracker.gpsassistant.db.pantograph

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbHelper
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManagerPantograph(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }

    // Функции для опускания токоприемников четных поездов

    fun insertToDbPantographChet(startPantographChet: Int, picketStartPantographChet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_PANTOGRAPH_CHET, startPantographChet)
            put(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_CHET, picketStartPantographChet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_PANTOGRAPH_CHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataPantographChet() : ArrayList<ListItemPantographChet> = withContext(Dispatchers.IO) {
        val dataListPantographChet = ArrayList<ListItemPantographChet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_PANTOGRAPH_CHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartPantographChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_PANTOGRAPH_CHET))
            val dataPicketStartPantographChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_CHET))
            val dataIdPantographChet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemPantographChet = ListItemPantographChet()
            itemPantographChet.startChet = dataStartPantographChet
            itemPantographChet.picketStartChet = dataPicketStartPantographChet
            itemPantographChet.idChet = dataIdPantographChet
            dataListPantographChet.add(itemPantographChet)
        }
        cursor.close()

        return@withContext dataListPantographChet
    }

    fun updateDbDataPantographChet(startPantographChet: Int, picketStartPantographChet: Int, idPantographChet: Int){
        val selection = BaseColumns._ID + "=$idPantographChet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_PANTOGRAPH_CHET, startPantographChet)
            put(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_CHET, picketStartPantographChet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_PANTOGRAPH_CHET, values, selection, null)
    }

    fun deleteDbDataPantographChet(idPantographChet: Int){
        val selection = BaseColumns._ID + "=$idPantographChet"
        db?.delete(MyDbNameClass.TABLE_NAME_PANTOGRAPH_CHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//

    // Функции для опускания токоприемников нечетных поездов

    fun insertToDbPantographNechet(startPantographNechet: Int, picketStartPantographNechet: Int){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_PANTOGRAPH_NECHET, startPantographNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_NECHET, picketStartPantographNechet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_PANTOGRAPH_NECHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataPantographNechet() : ArrayList<ListItemPantographNechet> = withContext(Dispatchers.IO) {
        val dataListPantographNechet = ArrayList<ListItemPantographNechet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_PANTOGRAPH_NECHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartPantographNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_PANTOGRAPH_NECHET))
            val dataPicketStartPantographNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_NECHET))
            val dataIdPantographNechet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemPantographNechet = ListItemPantographNechet()
            itemPantographNechet.startNechet = dataStartPantographNechet
            itemPantographNechet.picketStartNechet = dataPicketStartPantographNechet
            itemPantographNechet.idNechet = dataIdPantographNechet
            dataListPantographNechet.add(itemPantographNechet)
        }
        cursor.close()

        return@withContext dataListPantographNechet
    }

    fun updateDbDataPantographNechet(startPantographNechet: Int, picketStartPantographNechet: Int, idPantographNechet: Int){
        val selection = BaseColumns._ID + "=$idPantographNechet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_PANTOGRAPH_NECHET, startPantographNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_PANTOGRAPH_NECHET, picketStartPantographNechet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_PANTOGRAPH_NECHET, values, selection, null)
    }

    fun deleteDbDataPantographNechet(idPantographNechet: Int){
        val selection = BaseColumns._ID + "=$idPantographNechet"
        db?.delete(MyDbNameClass.TABLE_NAME_PANTOGRAPH_NECHET, selection, null)
    }

    //---------------------------------------------------------------------------------------------------//



    fun  closeDb(){
        myDbHelper.close()
    }
}