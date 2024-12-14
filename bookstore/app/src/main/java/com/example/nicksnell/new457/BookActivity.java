package com.example.nicksnell.new457;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicksnell.new457.database.MySQLiteHelper;

public class BookActivity extends Activity {

    private String name;
    private String title;
    private static final String TAG = "BookActivity";

    private MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        title = intent.getStringExtra("title");

        dbHelper = new MySQLiteHelper(this);

        fetchBookInfo();
    }

    private void fetchBookInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT ISBN, title, price FROM book WHERE title = ?";
        Cursor cursor = db.rawQuery(query, new String[]{title});

        if (cursor.moveToFirst()) {
            String bookISBN = cursor.getString(cursor.getColumnIndexOrThrow("ISBN"));
            String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String bookPrice = cursor.getString(cursor.getColumnIndexOrThrow("price"));

            TextView textView8 = findViewById(R.id.textView8);
            String bookInfo = "ISBN: " + bookISBN + "\nTitle: " + bookTitle + "\nPrice: " + bookPrice;
            textView8.setText(bookInfo);
        } else {
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    public void OnBackButton(View view) {
        Intent intent = new Intent(BookActivity.this, HomeActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
