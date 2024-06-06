package com.mikhail_R_gps_tracker.gpsassistant.db.redacktor

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbHelper
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManagerRedacktor(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }

    // Функции для редактирования киллометров четных поездов

    fun insertToDbRedacktorChet(startRedacktorChet: Int, picketStartRedacktorChet: Int, minusRedacktorChet: String, plusRedacktorChet: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_REDACKTOR_CHET, startRedacktorChet)
            put(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_CHET, picketStartRedacktorChet)
            put(MyDbNameClass.COLUMN_MINUS_REDACKTOR_CHET, minusRedacktorChet)
            put(MyDbNameClass.COLUMN_PLUS_REDACKTOR_CHET, plusRedacktorChet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_REDACKTOR_CHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataRedacktorChet() : ArrayList<ListItemRedacktorChet> = withContext(Dispatchers.IO) {
        val dataListRedacktorChet = ArrayList<ListItemRedacktorChet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_REDACKTOR_CHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartRedacktorChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_REDACKTOR_CHET))
            val dataPicketStartRedacktorChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_CHET))
            val dataMinusRedacktorChet = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_MINUS_REDACKTOR_CHET))
            val dataPlusRedacktorChet = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_PLUS_REDACKTOR_CHET))
            val dataIdRedacktorChet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemRedacktorChet = ListItemRedacktorChet()
            itemRedacktorChet.startChet = dataStartRedacktorChet
            itemRedacktorChet.picketStartChet = dataPicketStartRedacktorChet
            itemRedacktorChet.minusChet = dataMinusRedacktorChet
            itemRedacktorChet.plusChet = dataPlusRedacktorChet
            itemRedacktorChet.idChet = dataIdRedacktorChet
            dataListRedacktorChet.add(itemRedacktorChet)
        }
        cursor.close()

        return@withContext dataListRedacktorChet
    }

    fun updateDbDataRedacktorChet(startRedacktorChet: Int, picketStartRedactorChet: Int, minusRedacktorChet: String, plusRedacktorChet: String, idRedactorChet: Int){
        val selection = BaseColumns._ID + "=$idRedactorChet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_REDACKTOR_CHET, startRedacktorChet)
            put(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_CHET, picketStartRedactorChet)
            put(MyDbNameClass.COLUMN_MINUS_REDACKTOR_CHET, minusRedacktorChet)
            put(MyDbNameClass.COLUMN_PLUS_REDACKTOR_CHET, plusRedacktorChet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_REDACKTOR_CHET, values, selection, null)
    }

    fun deleteDbDataRedactorChet(idRedactorChet: Int){
        val selection = BaseColumns._ID + "=$idRedactorChet"
        db?.delete(MyDbNameClass.TABLE_NAME_REDACKTOR_CHET, selection, null)
    }

    //--------------------------------------------------------------------------------//

    // Функции для редактирования киллометров нечетных поездов

    fun insertToDbRedacktorNechet(startRedacktorNechet: Int, picketStartRedacktorNechet: Int, minusRedacktorNechet: String, plusRedacktorNechet: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_REDACKTOR_NECHET, startRedacktorNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_NECHET, picketStartRedacktorNechet)
            put(MyDbNameClass.COLUMN_MINUS_REDACKTOR_NECHET, minusRedacktorNechet)
            put(MyDbNameClass.COLUMN_PLUS_REDACKTOR_NECHET, plusRedacktorNechet)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_REDACKTOR_NECHET, null, values)
    }

    @SuppressLint("Range")
    suspend fun readDbDataRedacktorNechet() : ArrayList<ListItemRedacktorNechet> = withContext(Dispatchers.IO) {
        val dataListRedacktorNechet = ArrayList<ListItemRedacktorNechet>()

        val cursor = db?.query(MyDbNameClass.TABLE_NAME_REDACKTOR_NECHET, null,null,null,null,null,null)

        while (cursor?.moveToNext()!!){
            val dataStartRedacktorNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_START_REDACKTOR_NECHET))
            val dataPicketStartRedacktorNechet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_NECHET))
            val dataMinusRedacktorNechet = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_MINUS_REDACKTOR_NECHET))
            val dataPlusRedacktorNechet = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_PLUS_REDACKTOR_NECHET))
            val dataIdRedacktorNechet = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val itemRedacktorNechet = ListItemRedacktorNechet()
            itemRedacktorNechet.startNechet = dataStartRedacktorNechet
            itemRedacktorNechet.picketStartNechet = dataPicketStartRedacktorNechet
            itemRedacktorNechet.minusNechet = dataMinusRedacktorNechet
            itemRedacktorNechet.plusNechet = dataPlusRedacktorNechet
            itemRedacktorNechet.idNechet = dataIdRedacktorNechet
            dataListRedacktorNechet.add(itemRedacktorNechet)
        }
        cursor.close()

        return@withContext dataListRedacktorNechet
    }

    fun updateDbDataRedacktorNechet(startRedacktorNechet: Int, picketStartRedactorNechet: Int, minusRedacktorNechet: String, plusRedacktorNechet: String, idRedactorNechet: Int){
        val selection = BaseColumns._ID + "=$idRedactorNechet"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_START_REDACKTOR_NECHET, startRedacktorNechet)
            put(MyDbNameClass.COLUMN_PICKET_START_REDACKTOR_NECHET, picketStartRedactorNechet)
            put(MyDbNameClass.COLUMN_MINUS_REDACKTOR_NECHET, minusRedacktorNechet)
            put(MyDbNameClass.COLUMN_PLUS_REDACKTOR_NECHET, plusRedacktorNechet)
        }
        db?.update(MyDbNameClass.TABLE_NAME_REDACKTOR_NECHET, values, selection, null)
    }

    fun deleteDbDataRedactorNechet(idRedactorNechet: Int){
        val selection = BaseColumns._ID + "=$idRedactorNechet"
        db?.delete(MyDbNameClass.TABLE_NAME_REDACKTOR_NECHET, selection, null)
    }

    //--------------------------------------------------------------------------------//

    fun  closeDb(){
        myDbHelper.close()
    }
}