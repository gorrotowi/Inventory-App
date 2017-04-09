package com.gorrotowi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gorro on 09/04/17.
 */

public class InventoryOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Inventory.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " (" +
                    InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    InventoryContract.InventoryEntry.COLUMN_NAME_PRICE + INTEGER_TYPE + COMMA_SEP +
                    InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY + INTEGER_TYPE + COMMA_SEP +
                    InventoryContract.InventoryEntry.COLUMN_NAME_MAIL + TEXT_TYPE + COMMA_SEP +
                    InventoryContract.InventoryEntry.COLUMN_NAME_PHOTO + BLOB_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME;

    public InventoryOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
