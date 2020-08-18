package com.example.maps.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PontosDB extends SQLiteOpenHelper {

    private static String DBNAME = "pontos";
    private static int VERSION = 1;
    public static final String FIELD_ROW_ID = "_id";
    public static final String FIELD_LAT = "lat";
    public static final String FIELD_LNG = "lng";
    public static final String FIELD_ZOOM = "zom";
    public static final String titulo = "titulo";
    public static final String descricao = "descricao";
    public static final Boolean visitado = false;

    private static final String DATABASE_TABLE = "locations";


    private SQLiteDatabase DB;

    public PontosDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.DB = getWritableDatabase();
    }


    public PontosDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public PontosDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public PontosDB(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_LNG + " double , " +
                FIELD_LAT + " double , " +
                titulo + " String , " +
                descricao + " String , " +
                visitado + " boolean , " +
                FIELD_ZOOM + " text " +
                " ) ";

        db.execSQL(sql);
    }

    public long insert(ContentValues contentValues) {
        long rowID = DB.insert(DATABASE_TABLE, null, contentValues);
        return rowID;

    }

    public int del() {
        int cnt = DB.delete(DATABASE_TABLE, null, null);
        return cnt;
    }

    public Cursor getAllLocations() {
        return DB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_LAT, FIELD_LNG, FIELD_ZOOM, titulo, descricao, String.valueOf(visitado)}, null, null, null, null, null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
