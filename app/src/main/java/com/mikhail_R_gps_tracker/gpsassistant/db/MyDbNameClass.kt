package com.mikhail_R_gps_tracker.gpsassistant.db

import android.provider.BaseColumns

object MyDbNameClass {

    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "MyDatabase.db"

    // Таблица для сохранения редактирования киллометров четных поездов

    const val TABLE_NAME_REDACKTOR_CHET = "table_redacktor_chet"
    const val COLUMN_START_REDACKTOR_CHET = "start_redacktor_chet"
    const val COLUMN_PICKET_START_REDACKTOR_CHET = "picket_start_redacktor_chet"
    const val COLUMN_MINUS_REDACKTOR_CHET = "minus_redacktor_chet"
    const val COLUMN_PLUS_REDACKTOR_CHET = "plus_redacktor_chet"

    const val CREATE_TABLE_REDACKTOR_CHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_REDACKTOR_CHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_REDACKTOR_CHET TEXT, $COLUMN_PICKET_START_REDACKTOR_CHET TEXT, $COLUMN_MINUS_REDACKTOR_CHET TEXT," +
            "$COLUMN_PLUS_REDACKTOR_CHET TEXT)"
    const val SQL_DELETE_TABLE_REDACKTOR_CHET = "DROP TABLE IF EXISTS $TABLE_NAME_REDACKTOR_CHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения редактирования киллометров нечетных поездов

    const val TABLE_NAME_REDACKTOR_NECHET = "table_redacktor_nechet"
    const val COLUMN_START_REDACKTOR_NECHET = "start_redacktor_nechet"
    const val COLUMN_PICKET_START_REDACKTOR_NECHET = "picket_start_redacktor_nechet"
    const val COLUMN_MINUS_REDACKTOR_NECHET = "minus_redacktor_nechet"
    const val COLUMN_PLUS_REDACKTOR_NECHET = "plus_redacktor_nechet"

    const val CREATE_TABLE_REDACKTOR_NECHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_REDACKTOR_NECHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_REDACKTOR_NECHET TEXT, $COLUMN_PICKET_START_REDACKTOR_NECHET TEXT, $COLUMN_MINUS_REDACKTOR_NECHET TEXT," +
            "$COLUMN_PLUS_REDACKTOR_NECHET)"
    const val SQL_DELETE_TABLE_REDACKTOR_NECHET = "DROP TABLE IF EXISTS $TABLE_NAME_REDACKTOR_NECHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения ограничения скорости четных поездов

    const val TABLE_NAME_LIMITATIONS_CHET = "table_limitations_chet"
    const val COLUMN_START_LIMITATIONS_CHET = "start_limitations_chet"
    const val COLUMN_PICKET_START_LIMITATIONS_CHET = "picket_start_limitations_chet"
    const val COLUMN_FINISH_LIMITATIONS_CHET = "finish_limitations_chet"
    const val COLUMN_PICKET_FINISH_LIMITATIONS_CHET = "picket_finish_limitations_chet"
    const val COLUMN_SPEED_LIMITATIONS_CHET = "speed_limitations_chet"

    const val CREATE_TABLE_LIMITATIONS_CHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_LIMITATIONS_CHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_LIMITATIONS_CHET TEXT, $COLUMN_PICKET_START_LIMITATIONS_CHET TEXT," +
            "$COLUMN_FINISH_LIMITATIONS_CHET TEXT, $COLUMN_PICKET_FINISH_LIMITATIONS_CHET TEXT, $COLUMN_SPEED_LIMITATIONS_CHET TEXT)"
    const val SQL_DELETE_TABLE_LIMITATIONS_CHET = "DROP TABLE IF EXISTS $TABLE_NAME_LIMITATIONS_CHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения ограничения скорости нечетных поездов

    const val TABLE_NAME_LIMITATIONS_NECHET = "table_limitations_nechet"
    const val COLUMN_START_LIMITATIONS_NECHET = "start_limitations_nechet"
    const val COLUMN_PICKET_START_LIMITATIONS_NECHET = "picket_start_limitations_nechet"
    const val COLUMN_FINISH_LIMITATIONS_NECHET = "finish_limitations_nechet"
    const val COLUMN_PICKET_FINISH_LIMITATIONS_NECHET = "picket_finish_limitations_nechet"
    const val COLUMN_SPEED_LIMITATIONS_NECHET = "speed_limitations_nechet"

    const val CREATE_TABLE_LIMITATIONS_NECHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_LIMITATIONS_NECHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_LIMITATIONS_NECHET TEXT, $COLUMN_PICKET_START_LIMITATIONS_NECHET TEXT," +
            "$COLUMN_FINISH_LIMITATIONS_NECHET TEXT, $COLUMN_PICKET_FINISH_LIMITATIONS_NECHET TEXT, $COLUMN_SPEED_LIMITATIONS_NECHET TEXT)"
    const val SQL_DELETE_TABLE_LIMITATIONS_NECHET = "DROP TABLE IF EXISTS $TABLE_NAME_LIMITATIONS_NECHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения опускания токоприемников четных поездов

    const val TABLE_NAME_PANTOGRAPH_CHET = "table_pantograph_chet"
    const val COLUMN_START_PANTOGRAPH_CHET = "start_pantograph_chet"
    const val COLUMN_PICKET_START_PANTOGRAPH_CHET = "picket_start_pantograph_chet"

    const val CREATE_TABLE_PANTOGRAPH_CHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PANTOGRAPH_CHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_PANTOGRAPH_CHET TEXT, $COLUMN_PICKET_START_PANTOGRAPH_CHET TEXT)"
    const val SQL_DELETE_TABLE_PANTOGRAPH_CHET = "DROP TABLE IF EXISTS $TABLE_NAME_PANTOGRAPH_CHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения опускания токоприемников нечетных поездов

    const val TABLE_NAME_PANTOGRAPH_NECHET = "table_pantograph_nechet"
    const val COLUMN_START_PANTOGRAPH_NECHET = "start_pantograph_nechet"
    const val COLUMN_PICKET_START_PANTOGRAPH_NECHET = "picket_start_pantograph_nechet"

    const val CREATE_TABLE_PANTOGRAPH_NECHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PANTOGRAPH_NECHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_PANTOGRAPH_NECHET TEXT, $COLUMN_PICKET_START_PANTOGRAPH_NECHET TEXT)"
    const val SQL_DELETE_TABLE_PANTOGRAPH_NECHET = "DROP TABLE IF EXISTS $TABLE_NAME_PANTOGRAPH_NECHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения торможения четных поездов

    const val TABLE_NAME_BRAKE_CHET = "table_brake_chet"
    const val COLUMN_START_BRAKE_CHET = "start_brake_chet"
    const val COLUMN_PICKET_START_BRAKE_CHET = "picket_start_brake_chet"

    const val CREATE_TABLE_BRAKE_CHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_BRAKE_CHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_BRAKE_CHET TEXT, $COLUMN_PICKET_START_BRAKE_CHET TEXT)"
    const val SQL_DELETE_TABLE_BRAKE_CHET = "DROP TABLE IF EXISTS $TABLE_NAME_BRAKE_CHET"

    //--------------------------------------------------------------------------//

    // Таблица для сохранения торможения нечетных поездов

    const val TABLE_NAME_BRAKE_NECHET = "table_brake_nechet"
    const val COLUMN_START_BRAKE_NECHET = "start_brake_nechet"
    const val COLUMN_PICKET_START_BRAKE_NECHET = "picket_start_brake_nechet"

    const val CREATE_TABLE_BRAKE_NECHET = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_BRAKE_NECHET (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_START_BRAKE_NECHET TEXT, $COLUMN_PICKET_START_BRAKE_NECHET TEXT)"
    const val SQL_DELETE_TABLE_BRAKE_NECHET = "DROP TABLE IF EXISTS $TABLE_NAME_BRAKE_NECHET"
}