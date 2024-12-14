package com.example.nicksnell.new457;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.nicksnell.new457.database.MySQLiteHelper;

public class SigninActivity extends AsyncTask<String, Void, Integer> {
    private Context context;
    private MySQLiteHelper dbHelper;
    private String name;
    private int actionType;

    public SigninActivity(Context context, int actionType) {
        this.context = context;
        this.actionType = actionType;
        this.dbHelper = new MySQLiteHelper(context);
    }

    @Override
    protected Integer doInBackground(String... params) {
        name = params[0];

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (actionType == 1) {
            Cursor cursor = db.rawQuery("SELECT * FROM customer WHERE name = ?", new String[]{name});
            if (cursor.moveToFirst()) {
                cursor.close();
                return 2;
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("name", name);
            db.insert("customer", null, values);
            return 1;
        } else {
            Cursor cursor = db.rawQuery("SELECT * FROM customer WHERE name = ?", new String[]{name});
            boolean success = cursor.moveToFirst();
            cursor.close();
            return success ? 1 : 0;
        }
    }

@Override
    protected void onPostExecute(Integer result) {
        if (result.equals(1)) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("name", name);
            context.startActivity(intent);
        } else if (result.equals(0)) {
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show();
        } else if (result.equals(2)) {
            Toast.makeText(context, "User Already Exists", Toast.LENGTH_SHORT).show();
        }
    }
}

