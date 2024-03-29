package com.example.maps.controller;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.example.maps.db.PontosDB;

public class Controle extends ContentProvider {

    public static final String PROVIDER_NAME = "com.example.android.maps.locations";


    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations" );
    private static final int LOCATIONS = 1;

    private static final UriMatcher uriMatcher ;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
    }


    PontosDB mLocationsDB;


    @Override
    public boolean onCreate() {
        mLocationsDB = new PontosDB(getContext());
        return true;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mLocationsDB.insert(values);
        Uri _uri=null;
        if(rowID>0){
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        }else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }




    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = 0;
        cnt = mLocationsDB.del();
        return cnt;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(uriMatcher.match(uri)==LOCATIONS){
            return mLocationsDB.getAllLocations();
        }
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }
}

