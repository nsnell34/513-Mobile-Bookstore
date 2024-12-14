package com.example.nicksnell.new457.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "applicationdata.db";
    private static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE_BOOK =
            "CREATE TABLE book (" +
                    "ISBN CHAR(10) PRIMARY KEY, " +
                    "title VARCHAR(45) NOT NULL, " +
                    "price FLOAT NOT NULL);";

    public static final String CREATE_TABLE_CUSTOMER =
            "CREATE TABLE customer (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(45) NOT NULL, " +
                    "total_spent FLOAT NOT NULL DEFAULT 0);";

    public static final String CREATE_TABLE_PURCHASES =
            "CREATE TABLE purchases (" +
                    "purchaseID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "custID INTEGER NOT NULL, " +
                    "ISBN CHAR(10) NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "title VARCHAR(45) NOT NULL, " +
                    "FOREIGN KEY (custID) REFERENCES customer(ID) ON DELETE CASCADE, " +
                    "FOREIGN KEY (ISBN) REFERENCES book(ISBN) ON DELETE CASCADE);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOK);
        db.execSQL(CREATE_TABLE_CUSTOMER);
        db.execSQL(CREATE_TABLE_PURCHASES);

        String insertAdmin = "INSERT INTO customer (ID, name, total_spent) VALUES (NULL, 'admin', 0);";
        db.execSQL(insertAdmin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion +
                        ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS purchases;");
        db.execSQL("DROP TABLE IF EXISTS customer;");
        db.execSQL("DROP TABLE IF EXISTS book;");

        onCreate(db);
    }
}
