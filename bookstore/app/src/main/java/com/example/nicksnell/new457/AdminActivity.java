package com.example.nicksnell.new457;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nicksnell.new457.database.MySQLiteHelper;

public class AdminActivity extends Activity{
    private static final String TAG = "AdminActivity";
    private MySQLiteHelper dbHelper;
    public EditText isbn;
    public EditText price;
    public EditText title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        isbn = (EditText) findViewById(R.id.isbn);
        title = (EditText) findViewById(R.id.title);
        price = (EditText) findViewById(R.id.price);

        dbHelper = new MySQLiteHelper(this);
    }
    public void AddBook(View view) {
        String isbnText = isbn.getText().toString().trim();
        String titleText = title.getText().toString().trim();
        String priceText = price.getText().toString().trim();

        if (isbnText.isEmpty() || titleText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isbnText.length() != 10) {
            Toast.makeText(this, "ISBN must be 10 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        float priceValue;
        try {
            priceValue = Float.parseFloat(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String insertQuery = "INSERT INTO book (isbn, title, price) VALUES (?, ?, ?);";
            db.execSQL(insertQuery, new Object[]{isbnText, titleText, priceValue});
            Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show();

            isbn.setText("");
            title.setText("");
            price.setText("");
        } catch (Exception e) {
            Log.e(TAG, "Error adding book: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    public void Back(View view){
        Intent intent = new Intent( AdminActivity.this, HomeActivity.class );
        intent.putExtra("name", "admin");
        startActivity( intent );
    }
}