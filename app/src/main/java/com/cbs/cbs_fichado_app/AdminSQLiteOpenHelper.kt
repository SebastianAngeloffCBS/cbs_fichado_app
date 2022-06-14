package com.cbs.cbs_fichado_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?
                            , version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table usuario(idusuario text primary key, usuario text, password text,perfil text)")
        db.execSQL("create table persona(dni text primary key, nombre text, validado text )")
        db.execSQL("create table fichado( id    INTEGER PRIMARY KEY AUTOINCREMENT  ,dni text, nombre text, fechahora text, dimension text, sincronizado text,ciclo text, latitud text, longitud text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}