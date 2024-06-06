package com.mikhail_R_gps_tracker.gpsassistant.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.ListItemBrakeChet
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.ListItemBrakeNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.ListItemLimitationsChet
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.ListItemLimitationsNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.ListItemPantographChet
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.ListItemPantographNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.ListItemRedacktorChet
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.ListItemRedacktorNechet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {
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
            val dataPicketStartLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_START_LIMITATIONS_CHET))
            val dataFinishLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_FINISH_LIMITATIONS_CHET))
            val dataPicketFinishLimitationsChet: Int = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_PICKET_FINISH_LIMITATIONS_CHET))
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